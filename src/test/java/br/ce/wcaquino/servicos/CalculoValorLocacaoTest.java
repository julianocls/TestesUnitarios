package br.ce.wcaquino.servicos;

import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.servicos.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.servicos.exception.LocadoraException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static br.ce.wcaquino.builder.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builder.UsuarioBuilder.umUsuario;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {

    private LocacaoService service;

    private LocacaoDAO dao;
    private SpcServiceImpl spcService;

    @Before
    public void setup() {
        service = new LocacaoService();
        dao = Mockito.mock(LocacaoDAO.class);
        service.setLocaocaoDAO(dao);
        spcService = Mockito.mock(SpcServiceImpl.class);
        service.setSpcService(spcService);
    }

    @Parameter(value = 0)
    public List<Filme> filmes;

    @Parameter(value = 1)
    public Double desconto;

    @Parameter(value = 2)
    public String cenario;

    public static final Filme filme1 = umFilme().comValor(10.00).agora(); //new Filme("Filme 1", 2, 10.0);
    public static final Filme filme2 = umFilme().comValor(10.00).agora(); //new Filme("Filme 2", 5, 10.0);
    public static final Filme filme3 = umFilme().comValor(10.00).agora(); //new Filme("Filme 3", 5, 10.0);
    public static final Filme filme4 = umFilme().comValor(10.00).agora(); //new Filme("Filme 4", 5, 10.0);
    public static final Filme filme5 = umFilme().comValor(10.00).agora(); //new Filme("Filme 5", 5, 10.0);
    public static final Filme filme6 = umFilme().comValor(10.00).agora(); //new Filme("Filme 6", 5, 10.0);
    public static final Filme filme7 = umFilme().comValor(10.00).agora(); //new Filme("Filme 7", 5, 10.0);

    @Parameters(name = "{2}")
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
        Usuario usuario = umUsuario().agora(); //new Usuario("Usuario 1");

        // acao
        Locacao locacao = service.alugarFilme(usuario, filmes);

        // verificacao
        assertThat(locacao.getTotalDescontos(), is(desconto));
    }

}
