package org.delusion.elgame.world.generator;

import noise.OpenSimplexNoise;
import org.checkerframework.checker.units.qual.Temperature;

/**
 * Main class for generation of worlds.
 *
 * Determination Criteria:
 * - HistoricTemperature
 * - HistoricPressure
 * -
 * Calculate
 * - Humidity - based on pressure and humidity
 * - Pressure - fluctuates from base HistoricP
 * - Temperature - fluctuates from base HistoricT
 * Thoughts:
 *
 * - use a threshold with a noise map stretched over large areas & averaged into a per-chunk basis to make chunks into
 *   fault-line chunks which generate mountains. allows us to create some amount of tectonic simulation (earthquakes
 *   and volcanoes)
 * - 
 *
 *
 *
 */

public class WorldGenerator {
    //Determine seed
    private long seed = 123456;

    //Generate noise from seed
    private OpenSimplexNoise noiseTemperature = new OpenSimplexNoise(seed);
    private OpenSimplexNoise noisePressure = new OpenSimplexNoise(seed+1);

    //shit.
    public double historicTemperature(int x, int y) {
        return (noiseTemperature.eval(x/64.0, 1)+1)*35-25;
    }
    public double historicPressure(int x, int y) { return noisePressure.eval(x, y); }
}
