package br.ce.wcaquino.macher;

import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataLocacaoMatcher extends TypeSafeMatcher<Date> {

    private final Integer dias;

    public DataLocacaoMatcher(Integer dias) {
        this.dias = dias;
    }

    @Override
    protected boolean matchesSafely(Date data) {
        return DataUtils.isMesmaData(data, DataUtils.obterDataComDiferencaDias(dias));
    }

    @Override
    public void describeTo(Description description) {
        Date dataEsperada = DataUtils.obterDataComDiferencaDias(dias);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        description.appendText(dateFormat.format(dataEsperada));
    }
}
