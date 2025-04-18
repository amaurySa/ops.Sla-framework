package eu.atos.sla.evaluation.constraint.simple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import eu.atos.sla.evaluation.constraint.simple.Operator;
import eu.atos.sla.evaluation.constraint.simple.SimpleConstraintParser;
import eu.atos.sla.evaluation.constraint.simple.SimpleConstraintParser.SimpleConstraintElements;

public class SimpleConstraintParserTest{

	private void check(SimpleConstraintElements elems, String left, Operator op, String right) {
		
		assertEquals(left, elems.getLeft());
		assertEquals(op, elems.getOperator());
		assertEquals(right, elems.getRight());
	}
	
	private void checkException(SimpleConstraintParser parser, String constraint) {
		
		try {
			parser.parse(constraint);
			fail("constraint [" + constraint + "] should have failed");
		} catch (IllegalArgumentException e) {
			/* does nothing */
		}
	}
	
	@Test
	public void testParse() {
		
		SimpleConstraintParser parser = new SimpleConstraintParser();

		/*
		 * All simple operators
		 */
		check(parser.parse("m1 GT 1"), "m1", Operator.GT, "1");
		check(parser.parse("m1 GT (1)"), "m1", Operator.GT, "1");
		check(parser.parse("m2 GE 2"), "m2", Operator.GE, "2");
		check(parser.parse("m2 GE (2)"), "m2", Operator.GE, "2");
		check(parser.parse("m3 EQ 3"), "m3", Operator.EQ, "3");
		check(parser.parse("m3 EQ (3)"), "m3", Operator.EQ, "3");
		check(parser.parse("m4 LT 4"), "m4", Operator.LT, "4");
		check(parser.parse("m4 LT (4)"), "m4", Operator.LT, "4");
		check(parser.parse("m5 LE 5"), "m5", Operator.LE, "5");
		check(parser.parse("m5 LE (5)"), "m5", Operator.LE, "5");
		check(parser.parse("m6 NE 6"), "m6", Operator.NE, "6");
		check(parser.parse("m6 NE (6)"), "m6", Operator.NE, "6");

		/*
		 * Float value
		 */
		check(parser.parse("latency LT 0.5"), "latency", Operator.LT, "0.5");
		check(parser.parse("latency LT (0.5)"), "latency", Operator.LT, "0.5");

		/*
		 * In 
		 */
		check(parser.parse("val IN 1"), "val", Operator.IN, "1");
		check(parser.parse("val IN (1)"), "val", Operator.IN, "1");
		check(parser.parse("val IN (1,2)"), "val", Operator.IN, "1,2");
		check(parser.parse("val IN (1,2,3)"), "val", Operator.IN, "1,2,3");

		/*
		 * Between
		 */
		check(parser.parse("val BETWEEN (1,2)"), "val", Operator.BETWEEN, "1,2");
		
		/*
		 * Exists 
		 */
		check(parser.parse("val EXISTS"), "val", Operator.EXISTS, "");

		/*
		 * Not exists 
		 */
		check(parser.parse("val NOT_EXISTS"), "val", Operator.NOT_EXISTS, "");

		/*
		 * Remove spaces
		 */
		check(parser.parse("val BETWEEN  1,2"), "val", Operator.BETWEEN, "1,2");
		check(parser.parse("val BETWEEN 1,2 "), "val", Operator.BETWEEN, "1,2");
		check(parser.parse("val BETWEEN 1 ,2"), "val", Operator.BETWEEN, "1,2");
		check(parser.parse("val BETWEEN 1, 2"), "val", Operator.BETWEEN, "1,2");
		check(parser.parse("val IN  1 , 2 , 3 "), "val", Operator.IN, "1,2,3");
		check(parser.parse("val EXISTS  "), "val", Operator.EXISTS, "");		
		check(parser.parse("val NOT_EXISTS "), "val", Operator.NOT_EXISTS, "");
	}
	
	@Test
	public void testParseException() {
		SimpleConstraintParser parser = new SimpleConstraintParser();
		
		checkException(parser, "m1 TG 1");
		checkException(parser, "1 GT m1");
		checkException(parser, "1");
		checkException(parser, "1 GT");
		checkException(parser, "1 GT 2 3");
		checkException(parser, "val BETWEEN (1)");
		checkException(parser, "val BETWEEN (1,2,3)");
		checkException(parser, "val EXISTS (1)");
		checkException(parser, "val EXISTS ()");
		checkException(parser, "val EXISTS (1, 2)");
		checkException(parser, "val NOT_EXISTS (1)");
		checkException(parser, "val NOT_EXISTS ()");
		checkException(parser, "val NOT_EXISTS (1, 2)");
	}

}
