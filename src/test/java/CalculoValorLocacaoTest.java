import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.servicos.LocacaoService;
import br.ce.wcaquino.servicos.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.servicos.exception.LocadoraException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {

    private LocacaoService service;

    @Before
    public void setup() {
        service = new LocacaoService();
    }

    @Parameterized.Parameter(value = 0)
    public List<Filme> filmes;

    @Parameterized.Parameter(value = 1)
    public Double desconto;

    @Parameterized.Parameter(value = 2)
    public String cenario;

    public static final Filme filme1 = new Filme("Filme 1", 2, 10.0);
    public static final Filme filme2 = new Filme("Filme 2", 5, 10.0);
    public static final Filme filme3 = new Filme("Filme 3", 5, 10.0);
    public static final Filme filme4 = new Filme("Filme 4", 5, 10.0);
    public static final Filme filme5 = new Filme("Filme 5", 5, 10.0);
    public static final Filme filme6 = new Filme("Filme 6", 5, 10.0);
    public static final Filme filme7 = new Filme("Filme 7", 5, 10.0);

    @Parameterized.Parameters(name = "{2}")
    public static Collection<Object[]> getParametros() {
        return Arrays.asList(
                new Object[][]{
                        {Arrays.asList(filme1, filme2), 0.00, "2 Filmes - Desconto de 0%"},
                        {Arrays.asList(filme1, filme2, filme3), 2.5, "3 Filmes - Desconto de 25%"},
                        {Arrays.asList(filme1, filme2, filme3, filme4), 7.5, "4 Filmes - Desconto de 50%"},
                        {Arrays.asList(filme1, filme2, filme3, filme4, filme5), 15.00, "5 Filmes - Desconto de 75%"},
                        {Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 25.00, "6 Filmes - Desconto de 100%"},
                        {Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6, filme7), 25.00, "7 Filmes - Desconto de 0%"},
                }
        );
    }

    @Test
    public void deveCalcularValorLocacaoConsiderandoDesconto() throws FilmeSemEstoqueException, LocadoraException {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");

        // acao
        Locacao locacao = service.alugarFilme(usuario, filmes);

        // verificacao
        assertThat(locacao.getTotalDescontos(), is(desconto));
    }

}
