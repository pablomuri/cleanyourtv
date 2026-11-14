package com.gathin.tvhome;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

/** app_icons: which file names are read, how far a picture is sampled down, and which shape fills the tile. */
public class AppIconsTest {
    /** Twice the 180dp tile on a 1080p TV at density 1: what AppAdapter.decodeIcon() passes as the cap. */
    private static final int CAP = 360;

    @Test
    public void aNameInTheConfigFolderIsRead() {
        assertTrue(AppAdapter.iconName("netflix.png"));
        assertTrue(AppAdapter.iconName("icons/netflix.png"));
    }

    @Test
    public void aNameThatLeavesTheConfigFolderIsRefused() {
        assertFalse(AppAdapter.iconName("../../Download/netflix.png"));
        assertFalse(AppAdapter.iconName("icons/../../netflix.png"));
    }

    @Test
    public void noNameIsNoIcon() {
        assertFalse(AppAdapter.iconName(null));
        assertFalse(AppAdapter.iconName(""));
    }

    @Test
    public void aPictureThatFitsIsDecodedWhole() {
        assertEquals(1, AppAdapter.iconSampleSize(320, 180, CAP));
        assertEquals(1, AppAdapter.iconSampleSize(360, 360, CAP));
    }

    @Test
    public void aLargePictureIsSampledToBetweenOneAndTwoTileWidths() {
        assertEquals(2, AppAdapter.iconSampleSize(640, 360, CAP));
        assertEquals(8, AppAdapter.iconSampleSize(1920, 1080, CAP));
        assertEquals(8, AppAdapter.iconSampleSize(2560, 2560, CAP));
        for (int side : new int[] {361, 500, 719, 720, 721, 1280, 4096}) {
            int sampled = side / AppAdapter.iconSampleSize(side, side, CAP);
            assertTrue(side + " px came out at " + sampled, sampled >= CAP / 2 && sampled <= CAP);
        }
    }

    @Test
    public void theLongerSideDecidesForATallPicture() {
        assertEquals(4, AppAdapter.iconSampleSize(200, 1000, CAP));
    }

    @Test
    public void anUnreadableSizeOrCapLeavesTheSampleAtOne() {
        assertEquals(1, AppAdapter.iconSampleSize(-1, -1, CAP));
        assertEquals(1, AppAdapter.iconSampleSize(0, 0, CAP));
        assertEquals(1, AppAdapter.iconSampleSize(1920, 1080, 0));
    }

    @Test
    public void aWidePictureFillsTheTileAndASquareOneIsItsIcon() {
        assertTrue(AppAdapter.isBannerShaped(640, 360));
        assertFalse(AppAdapter.isBannerShaped(512, 512));
    }

    @Test
    public void entriesAreReadAsPackageToFile() {
        Map<String, String> icons = Config.packageMap(Arrays.asList(
                "com.netflix.ninja=icons/netflix.png", "com.example.tv = twitch.png ", "no-file=", "=orphan.png"));
        assertEquals(2, icons.size());
        assertEquals("icons/netflix.png", icons.get("com.netflix.ninja"));
        assertEquals("twitch.png", icons.get("com.example.tv"));
    }
}
