package com.robotica.util;

import java.util.Comparator;
import com.robotica.Ponto;

public class PontoComparador implements Comparator<Ponto> {
    @Override
    public int compare(Ponto a, Ponto b) {
        return Integer.compare(a.x, b.x);
    }
}
