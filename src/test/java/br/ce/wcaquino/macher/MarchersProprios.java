package br.ce.wcaquino.macher;

import org.hamcrest.Matcher;

import java.util.Calendar;

public class MarchersProprios {

    public static DiaSemanaMatcher caiEm(Integer diaSemana) {
        return new DiaSemanaMatcher(diaSemana);
    }

    public static DiaSemanaMatcher caiNumaSegunda() {
        return new DiaSemanaMatcher(Calendar.MONDAY);
    }

    public static DataLocacaoMatcher eHojeComDiferencaDias(Integer dias) {
        return new DataLocacaoMatcher(dias);
    }

    public static Matcher eHoje() {
        return new DataLocacaoMatcher(0);
    }
}
