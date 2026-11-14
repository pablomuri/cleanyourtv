package com.gathin.tvhome;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

/** app_banners image files: which shape fills the tile, how far a picture is sampled down, and how entries are read. */
public class AppBannerFilesTest {
    /** The 101 dp tile height on a 1080p Android TV (density 2), the square AppAdapter.fileIcon() samples to. */
    private static final int TILE_H = 202;

    @Test
    public void aWidePictureFillsTheTileAndASquareOneIsItsIcon() {
        assertTrue(AppAdapter.isBannerShaped(640, 360));
        assertFalse(AppAdapter.isBannerShaped(512, 512));
    }

    @Test
    public void aWidePictureKeepsAtLeastTheTileHeight() {
        int sample = WallpaperSize.sampleSize(1920, 1080, TILE_H, TILE_H);
        assertEquals(4, sample);
        assertTrue(1080 / sample >= TILE_H);
    }

    @Test
    public void aSquarePictureKeepsOnlyATileHighSquare() {
        assertEquals(2, WallpaperSize.sampleSize(512, 512, TILE_H, TILE_H));
        assertEquals(8, WallpaperSize.sampleSize(2048, 2048, TILE_H, TILE_H));
    }

    @Test
    public void aFileThatCouldNotBeMeasuredIsDecodedAsItIs() {
        // decodeFile() leaves the size at -1 for a missing file; the second decode then answers null.
        assertEquals(1, WallpaperSize.sampleSize(-1, -1, TILE_H, TILE_H));
    }

    @Test
    public void aPictureSmallerThanTheTileIsDecodedWhole() {
        assertEquals(1, WallpaperSize.sampleSize(320, 180, TILE_H, TILE_H));
        assertEquals(1, WallpaperSize.sampleSize(128, 128, TILE_H, TILE_H));
    }

    @Test
    public void aFileAndABundledBannerAreReadTheSameWay() {
        Map<String, String> banners = Config.packageMap(Arrays.asList(
                "com.example.app=icons/example.png", "com.example.tv = banner_tv ", "no-file=", "=orphan.png"));
        assertEquals(2, banners.size());
        assertEquals("icons/example.png", banners.get("com.example.app"));
        assertEquals("banner_tv", banners.get("com.example.tv"));
    }
}
