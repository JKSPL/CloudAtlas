package pl.edu.mimuw.cloudatlas.interpreter;

import java.util.ArrayList;

import pl.edu.mimuw.cloudatlas.model.Type;
import pl.edu.mimuw.cloudatlas.model.TypeCollection;
import pl.edu.mimuw.cloudatlas.model.Value;
import pl.edu.mimuw.cloudatlas.model.ValueBoolean;
import pl.edu.mimuw.cloudatlas.model.ValueList;
import pl.edu.mimuw.cloudatlas.model.ValueNull;

public class ResultList extends Result {

	private final Value list;
	
	public ResultList(Value l) {
		this.list = l;
	}
	
	@Override
	protected Result binaryOperationTyped(BinaryOperation operation, ResultSingle right) {
		if (this.getValue().isNull() || right.getValue().isNull())
			return new ResultList(ValueNull.getInstance());
		ArrayList<Value> newList = new ArrayList<Value>();
		for (Value v : getList())
			newList.add(operation.perform(v, right.getValue()));
		Type type = (newList.isEmpty() ? getElementType() : newList.get(0).getType());
		
		return new ResultList(new ValueList(newList, type));
	}
	
	@Override
	protected Result binaryOperationTyped(BinaryOperation operation, ResultColumn right) {
		throw new UnsupportedOperationException("Binary operations not supported on (ResultColumn, ResultList).");
	}
	
	@Override
	protected Result binaryOperationTyped(BinaryOperation operation, ResultList right) {
		throw new UnsupportedOperationException("Binary operations not supported on (ResultColumn, ResultList).");
	}

	@Override
	public Result unaryOperation(UnaryOperation operation) {
		if (this.getValue().isNull())
			return new ResultList(ValueNull.getInstance());
		ArrayList<Value> newList = new ArrayList<Value>();
		for (Value v : getList())
			newList.add(operation.perform(v));
		Type type = (newList.isEmpty() ? getElementType() : newList.get(0).getType());
		return new ResultList(new ValueList(newList, type));
	}

	@Override
	protected Result callMe(BinaryOperation operation, Result left) {
		return left.binaryOperationTyped(operation, this);
	}

	@Override
	public Value getValue() {
		return list;
	}

	@Override
	public ValueList getList() {
		return (ValueList) list;
	}

	@Override
	public ValueList getColumn() {
		throw new UnsupportedOperationException("Operation getList not supported on ResultList.");
	}

	@Override
	public Result filterNulls() {
		return new ResultList(Result.filterNullsList(getList()));
	}

	@Override
	public Result first(int size) {
		return new ResultSingle(Result.firstList(getList(), size));
	}

	@Override
	public Result last(int size) {
		return new ResultSingle(Result.lastList(getList(), size));
	}

	@Override
	public Result random(int size) {
		return new ResultSingle(Result.randomList(getList(), size));
	}

	@Override
	public Result convertTo(Type to) {
		ValueList newList = new ValueList(new ArrayList<Value>(), to);
		for (Value v : getList())
			newList.add(v.convertTo(to));
		return new ResultList(newList);
	}

	@Override
	public ResultSingle isNull() {
		return new ResultSingle(new ValueBoolean(list.isNull()));
	}

	@Override
	public Type getType() {
		return list.getType();
	}
	public Type getElementType()
	{
		return ((TypeCollection)list.getType()).getElementType();
	}


	@Override
	public ResultSingle aggregationOperation(AggregationOperation operation) {
		return new ResultSingle(operation.perform(getList()));
	}

	@Override
	public Result transformOperation(TransformOperation operation) {
		return new ResultList(operation.perform(getList()));
	}
}
