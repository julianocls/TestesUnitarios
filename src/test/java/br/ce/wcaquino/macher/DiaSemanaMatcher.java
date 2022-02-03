package br.ce.wcaquino.macher;

import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DiaSemanaMatcher extends TypeSafeMatcher<Date> {

    private final Integer diaSemana;

    public DiaSemanaMatcher(Integer diaSemana) {
        this.diaSemana = diaSemana;
    }

    protected boolean matchesSafely(Date data) {
        return DataUtils.verificarDiaSemana(data, diaSemana);
    }

    public void describeTo(Description description) {
        Calendar data = Calendar.getInstance();
        data.set(Calendar.DAY_OF_WEEK, diaSemana);
        String extenso = data.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("pt", "BR"));
        description.appendText(extenso);
    }

}
