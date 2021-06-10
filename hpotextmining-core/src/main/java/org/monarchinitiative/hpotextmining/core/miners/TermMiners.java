package org.monarchinitiative.hpotextmining.core.miners;

import org.monarchinitiative.hpotextmining.core.miners.biolark.BiolarkTermMiner;
import org.monarchinitiative.hpotextmining.core.miners.scigraph.SciGraphTermMiner;

import java.io.IOException;
import java.net.URL;

public class TermMiners {

    private TermMiners() {}

    public static TermMiner biolark(URL biolarkService) throws IOException {
        return new BiolarkTermMiner(biolarkService);
    }

    public static TermMiner scigraph(URL scigraphService) {
        return new SciGraphTermMiner(scigraphService);
    }

}
