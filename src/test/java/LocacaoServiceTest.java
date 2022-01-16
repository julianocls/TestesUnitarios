
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.servicos.LocacaoService;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.is;

import br.ce.wcaquino.servicos.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.servicos.exception.LocadoraException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class LocacaoServiceTest {

    private LocacaoService service;

    @Rule
    public ErrorCollector erro = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        service = new LocacaoService();
    }

    /**
     * Com CoreMatchers e sem CoreMatchers
     */
    @Test
    public void testLocacao_ValorMesmoValor() throws Exception {
        Locacao locacao = getLocacao();

        //verificação
        assertThat( locacao.getValor(), is(30.0));
        assertEquals(30.0, locacao.getValor(), 0.01);
    }

    /**
     * Com CoreMatchers e sem CoreMatchers
     */
    @Test
    public void testLocacao_MesmaData() throws Exception {
        Locacao locacao = getLocacao();

        //verificação
        assertThat( isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        assertTrue( isMesmaData(locacao.getDataLocacao(), new Date()));
    }

    /**
     * Com CoreMatchers e sem CoreMatchers
     */
    @Test
    public void testLocacao_MesmaDataComDiasDiferenca() throws Exception {
        Locacao locacao = getLocacao();

        //verificação
        assertThat( isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
        assertTrue( isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)));
    }

    /**
     * Com ErrorCollector
     */
    @Test
    public void testLocacao() throws Exception {
        Locacao locacao = getLocacao();

        //verificação
        erro.checkThat( locacao.getValor(), is(30.0));
        erro.checkThat( isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        erro.checkThat( isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
    }

    private Locacao getLocacao() throws Exception {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme1 = new Filme("Filme 1", 2, 5.0);
        Filme filme2 = new Filme("Filme 2", 5, 25.0);

        List<Filme> filmes = Arrays.asList(filme1, filme2);

        //acao
        return service.alugarFilme(usuario, filmes);
    }

    /**
     * Teste elegante
     */
    @Test(expected = FilmeSemEstoqueException.class)
    public void testLocacao_filmeSemEstoque_1() throws Exception {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme1 = new Filme("Filme 1", 0, 5.0);
        Filme filme2 = new Filme("Filme 2", 0, 25.0);

        List<Filme> filmes = Arrays.asList(filme1, filme2);

        //acao
        Locacao locacao = service.alugarFilme(usuario, filmes);
    }

    /**
     * Teste robusto
     */
    @Test
    public void testLocacao_filmeSemEstoque_2() {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme1 = new Filme("Filme 1", 0, 5.0);
        Filme filme2 = new Filme("Filme 2", 0, 25.0);

        List<Filme> filmes = Arrays.asList(filme1, filme2);

        //acao
        try {
            Locacao locacao = service.alugarFilme(usuario, filmes);

            Assert.fail("Deveria ter lançado uma exception");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Filme sem estoque"));
        }
    }

    /**
     * Teste elegante
     */
    @Test
    public void testLocacao_filmeSemEstoque_3() throws Exception {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme1 = new Filme("Filme 1", 0, 5.0);
        Filme filme2 = new Filme("Filme 2", 0, 25.0);

        List<Filme> filmes = Arrays.asList(filme1, filme2);

        exception.expect(Exception.class);
        exception.expectMessage("Filme sem estoque");

        //acao
        Locacao locacao = service.alugarFilme(usuario, filmes);
    }

    /**
     * Teste robusto
     */
    @Test
    public void testLocacao_usuarioVazio() throws FilmeSemEstoqueException {
        //cenario
        Usuario usuario = null; //new Usuario("Usuario 1");
        Filme filme1 = new Filme("Filme 1", 10, 5.0);
        Filme filme2 = new Filme("Filme 2", 30, 25.0);

        List<Filme> filmes = Arrays.asList(filme1, filme2);

        //acao
        try {
            service.alugarFilme(usuario, filmes);

            Assert.fail("Deveria ter lançado uma exception");
        } catch (LocadoraException e) {
            assertThat(e.getMessage(), is("Usuario vazio"));
        }
    }

    /**
     * Teste robusto
     */
    @Test
    public void testLocacao_filmeVazio() throws FilmeSemEstoqueException {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme1 = null; //new Filme("Filme 1", 2, 5.0);
        Filme filme2 = null; //new Filme("Filme 2", 5, 25.0);

        List<Filme> filmes = Arrays.asList(filme1, filme2);

        //acao
        try {
            service.alugarFilme(usuario, filmes);

            Assert.fail("Deveria ter lançado uma exception");
        } catch (LocadoraException e) {
            assertThat(e.getMessage(), is("Filme vazio"));
        }
    }
}
