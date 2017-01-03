package pl.edu.mimuw.cloudatlas.interpreter;

import java.util.ArrayList;

import pl.edu.mimuw.cloudatlas.model.Type;
import pl.edu.mimuw.cloudatlas.model.TypeCollection;
import pl.edu.mimuw.cloudatlas.model.Value;
import pl.edu.mimuw.cloudatlas.model.ValueBoolean;
import pl.edu.mimuw.cloudatlas.model.ValueList;
import pl.edu.mimuw.cloudatlas.model.ValueNull;

public class ResultColumn extends Result {
	
	// In order for the (column, list, single) type system to work, 
	// we will hold list value as well as null value in the same variable
	// holding null value only in singleResult would be destructive to type system
	private final Value column;
	
	public ResultColumn(Value list)
	{
		this.column = list;
	}

	@Override
	protected Result binaryOperationTyped(BinaryOperation operation, ResultSingle right) {
		if (this.getValue().isNull() || right.getValue().isNull())
			return new ResultColumn(ValueNull.getInstance());
		
		ArrayList<Value> newList = new ArrayList<Value>();
		for (Value v : getColumn())
			newList.add(operation.perform(v, right.getValue()));
		Type t = (newList.isEmpty() ? getElementType() : newList.get(0).getType());
		return new ResultColumn(new ValueList(newList, t));
	}
	
	@Override
	protected Result binaryOperationTyped(BinaryOperation operation, ResultColumn right) {
		if (this.getValue().isNull() || right.getValue().isNull())
			return new ResultColumn(ValueNull.getInstance());
		
		ArrayList<Value> newList = new ArrayList<Value>();
		ValueList other = right.getColumn();
		
		if (getColumn().size() != right.getColumn().size())
			throw new IllegalArgumentException("Columns have different size");
		
		for (int i = 0 ; i < getColumn().size(); i ++)
			newList.add(operation.perform(getColumn().get(i), other.get(i)));
		
		Type t = (newList.isEmpty() ? getElementType() : newList.get(0).getType());
		return new ResultColumn(new ValueList(newList, t));
	}

	@Override
	protected Result binaryOperationTyped(BinaryOperation operation, ResultList right) {
		throw new UnsupportedOperationException("Binary operations not supported on (ResultColumn, ResultList).");
	}

	@Override
	public Result unaryOperation(UnaryOperation operation) {
		if (this.getValue().isNull())
			return new ResultColumn(ValueNull.getInstance());
		
		ArrayList<Value> newList = new ArrayList<Value>();
		for (Value v : getColumn())
			newList.add(operation.perform(v));
		Type type = (newList.isEmpty() ? getElementType() : newList.get(0).getType());
		return new ResultColumn(new ValueList(newList, type));
	}

	@Override
	protected Result callMe(BinaryOperation operation, Result left) {
		return left.binaryOperationTyped(operation, this);
	}

	@Override
	public Value getValue() {
		return column;
	}

	@Override
	public ValueList getList() {
		throw new UnsupportedOperationException("Operation getList not supported on ResultSingle.");
	}

	@Override
	public ValueList getColumn() {
		return ((ValueList) column);
	}
	
	public Type getElementType()
	{
		return ((TypeCollection)column.getType()).getElementType();
	}

	@Override
	public Result filterNulls() {
		return new ResultColumn(filterNullsList(getColumn()));
	}

	@Override
	public Result first(int size) {
		return new ResultSingle(firstList(getColumn(), size));
	}

	@Override
	public Result last(int size) {
		return new ResultSingle(lastList(getColumn(), size));
	}

	@Override
	public Result random(int size) {
		return new ResultSingle(randomList(getColumn(), size));
	}

	@Override
	public Result convertTo(Type to) {
		ValueList newList = new ValueList(new ArrayList<Value>(), to);
		for (Value v : getColumn())
			newList.add(v.convertTo(to));
		return new ResultColumn(newList);
	}

	@Override
	public ResultSingle isNull() {
		return new ResultSingle(new ValueBoolean(column.isNull()));
	}

	@Override
	public Type getType() {
		return column.getType();
	}

	@Override
	public ResultSingle aggregationOperation(AggregationOperation operation) {
		if (getValue().isNull())
			return new ResultSingle(ValueNull.getInstance());
		return new ResultSingle(operation.perform(getColumn()));
	}

	@Override
	public Result transformOperation(TransformOperation operation) {
		if (getValue().isNull())
			return new ResultList(ValueNull.getInstance());
		return new ResultList(operation.perform(getColumn()));
	}
}
