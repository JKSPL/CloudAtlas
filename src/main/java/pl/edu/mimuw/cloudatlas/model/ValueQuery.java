package pl.edu.mimuw.cloudatlas.model;

public class ValueQuery extends Value {
	
	private final String query;
	
	public ValueQuery(String query) {
		this.query = query;
	}

	@Override
	public Type getType() {
		return TypePrimitive.QUERY;
	}

	@Override
	public boolean isNull() {
		return query == null;
	}

	@Override
	public Value convertTo(Type to) {
		switch(to.getPrimaryType()) {
			case QUERY:
				return this;
			case STRING:
				if(isNull())
					return ValueString.NULL_STRING;
				else
					return new ValueString(query);
			default:
				throw new UnsupportedConversionException(getType(), to);
		}
	}

	@Override
	public Value getDefaultValue() {
		return new ValueQuery(null);
	}
	
	
	private ValueQuery()
	{
		this.query = null;
	}
}
