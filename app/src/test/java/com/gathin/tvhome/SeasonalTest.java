package com.gathin.tvhome;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Test;

public class SeasonalTest {

    private static LocalDate d(int y, int m, int day) {
        return LocalDate.of(y, m, day);
    }

    @Test
    public void easterSundayMatchesKnownDates() {
        assertEquals(d(1999, 4, 4), Season.easterSunday(1999));
        assertEquals(d(2000, 4, 23), Season.easterSunday(2000));
        assertEquals(d(2024, 3, 31), Season.easterSunday(2024));
        assertEquals(d(2025, 4, 20), Season.easterSunday(2025));
        assertEquals(d(2026, 4, 5), Season.easterSunday(2026));
        assertEquals(d(2027, 3, 28), Season.easterSunday(2027));
        assertEquals(d(2038, 4, 25), Season.easterSunday(2038));
    }

    @Test
    public void halloweenRunsFromMidOctoberThroughTheFirstOfNovember() {
        assertNull(Season.on(d(2026, 10, 14)));
        assertSame(Season.HALLOWEEN, Season.on(d(2026, 10, 15)));
        assertSame(Season.HALLOWEEN, Season.on(d(2026, 10, 31)));
        assertSame(Season.HALLOWEEN, Season.on(d(2026, 11, 1)));
        assertNull(Season.on(d(2026, 11, 2)));
    }

    @Test
    public void christmasThenNewYearReachingIntoJanuary() {
        assertNull(Season.on(d(2026, 12, 9)));
        assertSame(Season.CHRISTMAS, Season.on(d(2026, 12, 10)));
        assertSame(Season.CHRISTMAS, Season.on(d(2026, 12, 26)));
        assertSame(Season.NEW_YEAR, Season.on(d(2026, 12, 27)));
        assertSame(Season.NEW_YEAR, Season.on(d(2026, 12, 31)));
        assertSame(Season.NEW_YEAR, Season.on(d(2027, 1, 1)));
        assertSame(Season.NEW_YEAR, Season.on(d(2027, 1, 2)));
        assertNull(Season.on(d(2027, 1, 3)));
    }

    @Test
    public void valentinesAndStPatricks() {
        assertNull(Season.on(d(2027, 2, 6)));
        assertSame(Season.VALENTINES, Season.on(d(2027, 2, 7)));
        assertSame(Season.VALENTINES, Season.on(d(2027, 2, 14)));
        assertNull(Season.on(d(2027, 2, 15)));
        assertSame(Season.ST_PATRICKS, Season.on(d(2027, 3, 10)));
        assertSame(Season.ST_PATRICKS, Season.on(d(2027, 3, 17)));
        assertNull(Season.on(d(2027, 3, 18)));
    }

    @Test
    public void easterCoversTheWeekBeforeAndEasterMonday() {
        // Easter 2026 is 5 April.
        assertNull(Season.on(d(2026, 3, 26)));
        assertSame(Season.EASTER, Season.on(d(2026, 3, 27)));
        assertSame(Season.EASTER, Season.on(d(2026, 4, 5)));
        assertSame(Season.EASTER, Season.on(d(2026, 4, 6)));
        assertNull(Season.on(d(2026, 4, 7)));
    }

    @Test
    public void anEarlyEasterLeavesStPatricksItsOwnDays() {
        // Easter 2008 is 23 March, so its window starts on 14 March, inside St. Patrick's week.
        assertSame(Season.ST_PATRICKS, Season.on(d(2008, 3, 14)));
        assertSame(Season.ST_PATRICKS, Season.on(d(2008, 3, 17)));
        assertSame(Season.EASTER, Season.on(d(2008, 3, 18)));
        assertSame(Season.EASTER, Season.on(d(2008, 3, 24)));
    }

    @Test
    public void anOrdinaryDayHasNoSeason() {
        assertNull(Season.on(d(2026, 6, 15)));
        assertNull(Season.on(d(2026, 8, 1)));
    }

    @Test
    public void everySeasonHasOneStableIdAndFindsItselfByIt() {
        for (Season s : Season.values()) {
            assertSame(s, Season.fromId(s.id));
            assertSame(s, Season.fromId(" " + s.id.toUpperCase() + " "));
        }
        assertSame(Season.ST_PATRICKS, Season.fromId("st-patricks"));
        assertSame(Season.NEW_YEAR, Season.fromId("new year"));
        assertNull(Season.fromId("diwali"));
        assertNull(Season.fromId(""));
        assertNull(Season.fromId(null));
    }

    @Test
    public void normaliseKeepsOffAutoAndSeasonIdsAndFallsBackOtherwise() {
        assertEquals("auto", Seasonal.normalise("  AUTO ", "off"));
        assertEquals("off", Seasonal.normalise("Off", "auto"));
        assertEquals("easter", Seasonal.normalise("Easter", "off"));
        assertEquals("st_patricks", Seasonal.normalise("st-patricks", "off"));
        assertEquals("off", Seasonal.normalise("bogus", "off"));
        assertEquals("auto", Seasonal.normalise(null, "auto"));
    }

    @Test
    public void theSavedChoiceBeatsTheThemeWhichBeatsConfigXml() {
        assertEquals("christmas", Seasonal.mode("christmas", "off", "auto", "auto"));
        assertEquals("off", Seasonal.mode("", "off", "auto", "auto"));
        assertEquals("easter", Seasonal.mode("", "", "easter", "auto"));
        assertEquals("auto", Seasonal.mode("", "", "", "auto"));
        assertEquals("off", Seasonal.mode("", "", "", "off"));
    }

    @Test
    public void anUnknownValueIsSkippedNotObeyed() {
        assertEquals("easter", Seasonal.mode("", "bogus", "easter", "off"));
        assertEquals("off", Seasonal.mode("nonsense", "", "", "off"));
        assertEquals("off", Seasonal.mode("", "", "", "bogus"));
    }

    @Test
    public void offGivesNoFestival() {
        assertSame(Festival.NONE, Seasonal.festival("off", d(2026, 10, 31)));
    }

    @Test
    public void automaticShowsTheDaysSeasonAndEndsWithItsWindow() {
        Festival f = Seasonal.festival("auto", d(2026, 10, 31));
        assertEquals("halloween", f.name);
        assertSame(Season.HALLOWEEN, f.season);
        assertTrue(f.seasonal);
        assertEquals(d(2026, 11, 1), f.until);
        assertTrue(f.active(d(2026, 11, 1)));
        assertFalse(f.active(d(2026, 11, 2)));
    }

    @Test
    public void automaticBetweenHolidaysShowsNothing() {
        assertSame(Festival.NONE, Seasonal.festival("auto", d(2026, 7, 20)));
    }

    @Test
    public void aKeptSeasonShowsOnAnyDayWithNoEndDate() {
        Festival f = Seasonal.festival("easter", d(2026, 7, 20));
        assertSame(Season.EASTER, f.season);
        assertEquals(null, f.until);
        assertTrue(f.active(d(2030, 1, 1)));
    }

    @Test
    public void aThemeNamingASeasonGetsItsSkinAndDefaultAccent() {
        Festival f = Festival.parse("Halloween", "", "", "");
        assertSame(Season.HALLOWEEN, f.season);
        assertFalse(f.seasonal);
        assertEquals(Season.HALLOWEEN.accent, f.accentColor());
        assertEquals(0xFF112233, Festival.parse("halloween", "", "", "#112233").accentColor());
    }

    @Test
    public void aFestivalThatIsNotASeasonKeepsTheDiyaSkin() {
        Festival f = Festival.parse("diwali", "", "", "");
        assertNull(f.season);
        assertEquals(Festival.DEFAULT_ACCENT, f.accentColor());
    }

    @Test
    public void aThemesOwnFestivalWinsWhileItRunsThenTheSeasonTakesOver() {
        Festival diwali = Festival.parse("diwali", "2026-11-10", "", "");
        // 31 October is Halloween in Automatic mode, but the theme's Diwali is still running.
        assertSame(diwali, Seasonal.pick(diwali, "auto", d(2026, 10, 31)));
        // After its end date the calendar decides again.
        Festival after = Seasonal.pick(diwali, "auto", d(2026, 12, 12));
        assertSame(Season.CHRISTMAS, after.season);
        assertSame(Festival.NONE, Seasonal.pick(diwali, "off", d(2026, 12, 12)));
        assertSame(Season.HALLOWEEN, Seasonal.pick(Festival.NONE, "auto", d(2026, 10, 31)).season);
    }

    @Test
    public void everySeasonHasAnOpaqueAccent() {
        for (Season s : Season.values()) assertEquals(0xFF000000, s.accent & 0xFF000000);
    }
}
