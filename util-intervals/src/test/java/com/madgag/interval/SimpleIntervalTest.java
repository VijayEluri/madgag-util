package com.madgag.interval;

import static com.madgag.interval.BeforeOrAfter.AFTER;
import static com.madgag.interval.BeforeOrAfter.BEFORE;
import static com.madgag.interval.Closure.CLOSED;
import static com.madgag.interval.Closure.OPEN;
import static com.madgag.interval.SimpleInterval.instantInterval;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class SimpleIntervalTest {

    @Test
	public void anIntervalIsNotAfterAnInternalPoint() {
		assertThat(new SimpleInterval(10, CLOSED, 20, OPEN).is(AFTER, 15), is(false));
    }

    @Test
	public void anIntervalIsNotBeforeAnInternalPoint() {
		assertThat(new SimpleInterval(10, OPEN, 20, CLOSED).is(BEFORE, 15), is(false));
    }


    @Test
	public void anIntervalIsAfterAPointToTheLeftOfItsBoundary() {
		assertThat(new SimpleInterval(10, CLOSED, 20, OPEN).is(AFTER, 9), is(true));
        assertThat(new SimpleInterval(10, OPEN, 20, OPEN).is(AFTER, 9), is(true));
    }

    @Test
	public void anIntervalIsBeforeAPointToTheRightOfItsBoundary() {
		assertThat(new SimpleInterval(10, OPEN, 20, CLOSED).is(BEFORE, 21), is(true));
        assertThat(new SimpleInterval(10, OPEN, 20, OPEN).is(BEFORE, 21), is(true));
    }

    @Test
	public void aLeftClosedIntervalIsNotAfterItsOwnLeftBound() {
		assertThat(new SimpleInterval(10, CLOSED, 20, OPEN).is(AFTER, 10), is(false));
    }

    @Test
	public void aRightClosedIntervalIsNotBeforeItsOwnRightBound() {
		assertThat(new SimpleInterval(10, OPEN, 20, CLOSED).is(BEFORE, 20), is(false));
    }

    @Test
	public void aLeftOpenIntervalIsAfterItsOwnLeftBound() {
		assertThat(new SimpleInterval(10, OPEN, 20, CLOSED).is(AFTER, 10), is(true));
    }

    @Test
	public void aRightOpenIntervalIsBeforeItsOwnRightBound() {
		assertThat(new SimpleInterval(10, CLOSED, 20, OPEN).is(BEFORE, 20), is(true));
    }

	@Test
	public void shouldReturnTrueIfIntervalContainsOtherInterval() {
		SimpleInterval<Integer> a = new SimpleInterval<Integer>(10,CLOSED ,20,OPEN );
		assertThat(a.contains(new SimpleInterval<Integer>(12,CLOSED ,18,OPEN )), equalTo(true));
		assertThat(a.contains(new SimpleInterval<Integer>(15,CLOSED ,25,OPEN )), equalTo(false));
		
		assertThat(new SimpleInterval<Integer>(10,CLOSED,20,CLOSED).contains(new SimpleInterval<Integer>(10,CLOSED,20,CLOSED)), equalTo(true));
		assertThat(new SimpleInterval<Integer>(10,OPEN,20,OPEN).contains(new SimpleInterval<Integer>(10,OPEN,20,OPEN)), equalTo(true));
		assertThat(new SimpleInterval<Integer>(10,CLOSED,20,CLOSED).contains(new SimpleInterval<Integer>(10,OPEN,20,OPEN)), equalTo(true));
		assertThat(new SimpleInterval<Integer>(10,OPEN,20,OPEN).contains(new SimpleInterval<Integer>(10,CLOSED,20,CLOSED)), equalTo(false));
		
	}
	
	@Test
	public void shouldReturnTrueIfPointIsContainedInInterval() {
		SimpleInterval<Integer> closed10open20 = new SimpleInterval<Integer>(10,CLOSED ,20,OPEN );
		assertThat(closed10open20.contains(10), equalTo(true));
		assertThat(closed10open20.contains(15), equalTo(true));
		assertThat(closed10open20.contains(20), equalTo(false));
		
		SimpleInterval<Integer> open10closed20 = new SimpleInterval<Integer>(10,OPEN ,20,CLOSED );
		assertThat(open10closed20.contains(10), equalTo(false));
		assertThat(open10closed20.contains(15), equalTo(true));
		assertThat(open10closed20.contains(20), equalTo(true));
	}

    @Test
	public void shouldNotReportAnIntervalIsBeforeAnotherIfTheyOverlap() {
        assertThat(new SimpleInterval<Integer>(10,20).is(BEFORE, new SimpleInterval<Integer>(15,16)), is(false));
        assertThat(new SimpleInterval<Integer>(15,16).is(BEFORE, new SimpleInterval<Integer>(10,20)), is(false));
	}

	@Test
	public void shouldReturnTrueIfIntervalsOverlap() {
		assertOverlap(true,new SimpleInterval<Integer>(10,20), new SimpleInterval<Integer>(15,16));
		assertOverlap(true,new SimpleInterval<Integer>(5,15), new SimpleInterval<Integer>(10,20));
		assertOverlap(false,new SimpleInterval<Integer>(0,5), new SimpleInterval<Integer>(10,20));
	}
	
	@Test
	public void shouldReturnFalseForOverlapIfIntervalsAbut() {
		assertOverlap(false,new SimpleInterval<Integer>(10,20), new SimpleInterval<Integer>(20,30));
	}

	@Test
	public void shouldRegardARightClosedIntervalAsBeforeALeftOpenInterval() throws Exception {
		SimpleInterval<Integer> rightClosed = new SimpleInterval<Integer>(10, CLOSED,20, CLOSED);
		SimpleInterval<Integer> leftOpen = new SimpleInterval<Integer>(20, OPEN,40, CLOSED);
        assertThat(leftOpen.is(AFTER, rightClosed), equalTo(true));
        assertThat(rightClosed.is(BEFORE, leftOpen), equalTo(true));
		assertThat(leftOpen.overlaps(rightClosed), equalTo(false));
		assertThat(rightClosed.overlaps(leftOpen), equalTo(false));
	}
	
	@Test
	public void shouldReturnTrueForOverlapUsingZeroLengthIntervals() {
		SimpleInterval<Integer> thickInterval = new SimpleInterval<Integer>(10,20);
		SimpleInterval<Integer> instantIntervalAtStartOfThick = instantInterval(10, CLOSED);
		SimpleInterval<Integer> instantIntervalAtEndOfThick = instantInterval(20, OPEN);
		
		assertOverlap(true, thickInterval, instantIntervalAtStartOfThick);
		assertOverlap(false,thickInterval, instantIntervalAtEndOfThick);
	}
	
	private void assertOverlap(boolean expectedOverlap, SimpleInterval<Integer> a, SimpleInterval<Integer> b) {
		assertThat(assertOverlapMessage(a, b), a.overlaps(b), equalTo(expectedOverlap));
		assertThat(assertOverlapMessage(b, a), b.overlaps(a), equalTo(expectedOverlap));
	}

	private String assertOverlapMessage(SimpleInterval<Integer> c,	SimpleInterval<Integer> d) {
        return "for c="+c+" overlaps d="+d+" (c<d="+ c.is(BEFORE, d) +" c>d="+ c.is(AFTER, d) +" d<c="+ d.is(BEFORE, c) +" d>c="+ d.is(AFTER, c) +")";
	}

}
