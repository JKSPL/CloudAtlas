package pl.edu.mimuw.cloudatlas.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class ConversionTest {

	@Test
	public void test() {
		Value ints[] = {
				new ValueInt(1L),
				new ValueInt(1L),
				new ValueInt(2L),
				new ValueInt(3L),
				new ValueInt(5L)
		};
		ValueSet setInt = new ValueSet(TypePrimitive.INTEGER);
		for(int i = 0; i < ints.length; i++){
			setInt.add(ints[i]);
		}
		ValueString setString = (ValueString) setInt.convertTo(TypePrimitive.STRING); 
		System.out.println("Set: " + setString.getValue());
		assertTrue(setString.getValue().equals("{1, 2, 3, 5}"));
		
		
		ValueList listInt = new ValueList(TypePrimitive.INTEGER);
		for(int i = 0; i < ints.length; i++){
			listInt.add(ints[i]);
		}
		ValueString listString = (ValueString) listInt.convertTo(TypePrimitive.STRING);
		System.out.println("List: " + listString.getValue());
		assertTrue(listString.getValue().equals("[1, 1, 2, 3, 5]"));
		
		
		Value nullValue = ValueNull.getInstance();
		ValueString nullString = (ValueString)nullValue.convertTo(TypePrimitive.STRING);
		System.out.println("Null: " + nullString.getValue());
		assertTrue(nullString.getValue().equals("NULL"));
		
		Value durationValue = new ValueDuration(5112323400l);
		ValueString durationString = (ValueString)durationValue.convertTo(TypePrimitive.STRING);
		System.out.println("Duration: " + durationString.getValue());
		
		Value timeValue = new ValueTime(123123231L);
		ValueString timeString = (ValueString)timeValue.convertTo(TypePrimitive.STRING);
		System.out.println("Time: " + timeString.getValue());
		//fail("Not yet implemented");
	}

}
