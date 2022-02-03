package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.servicos.LocacaoService;
import br.ce.wcaquino.servicos.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.servicos.exception.LocadoraException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import java.util.*;

import static br.ce.wcaquino.macher.MarchersProprios.*;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.is;
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
    public void deveSerOMesmoValor() throws Exception {
        Locacao locacao = getLocacao();

        //verificação
        assertThat(locacao.getValorPagamento(), is(30.0));
        assertEquals(30.0, locacao.getValorPagamento(), 0.01);
    }

    /**
     * Com CoreMatchers e sem CoreMatchers
     */
    @Test
    public void deveSerAMesmaData() throws Exception {
        Locacao locacao = getLocacao();

        //verificação
        assertThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        assertTrue(isMesmaData(locacao.getDataLocacao(), new Date()));
    }

    /**
     * Com CoreMatchers e sem CoreMatchers
     */
    @Test
    public void deveSerAMesmaDataComDiasDiferenca() throws Exception {
        Locacao locacao = getLocacao();

        //verificação
        assertThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
        assertTrue(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)));
    }

    /**
     * Com ErrorCollector
     */
    @Test
    public void deveAlugarFilme() throws Exception {
        Locacao locacao = getLocacao();

        //verificação
        erro.checkThat(locacao.getValorPagamento(), is(30.0));
        //erro.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        erro.checkThat(locacao.getDataLocacao(), eHoje());
        //erro.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
        erro.checkThat(locacao.getDataRetorno(), eHojeComDiferencaDias(1));
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
    public void naoDeveAlugarFilmeSemEstoque_LancarExcessao() throws Exception {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme1 = new Filme("Filme 1", 0, 5.0);
        Filme filme2 = new Filme("Filme 2", 0, 25.0);

        List<Filme> filmes = Arrays.asList(filme1, filme2);

        //acao
        service.alugarFilme(usuario, filmes);
    }

    /**
     * Teste robusto
     */
    @Test
    public void naoDeveAlugarFilmeSemEstoque_robusto() {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme1 = new Filme("Filme 1", 0, 5.0);
        Filme filme2 = new Filme("Filme 2", 0, 25.0);

        List<Filme> filmes = Arrays.asList(filme1, filme2);

        //acao
        try {
            service.alugarFilme(usuario, filmes);

            Assert.fail("Deveria ter lançado uma exception");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Filme sem estoque"));
        }
    }

    /**
     * Teste elegante
     */
    @Test
    public void naoDeveAlugarFilmeSemEstoque_elegante() throws Exception {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme1 = new Filme("Filme 1", 0, 5.0);
        Filme filme2 = new Filme("Filme 2", 0, 25.0);

        List<Filme> filmes = Arrays.asList(filme1, filme2);

        exception.expect(Exception.class);
        exception.expectMessage("Filme sem estoque");

        //acao
        service.alugarFilme(usuario, filmes);
    }

    /**
     * Teste robusto
     */
    @Test
    public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
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
    public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException {
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

    /* Comentado para utilizar o "Data Driven Test", "Parameterize" do junit na classe CalculoValorLocacaoTest
        @Test
        public void naoDeveTerDescontoNaLocacao() throws FilmeSemEstoqueException, LocadoraException {
            //cenario
            Usuario usuario = new Usuario("Usuario 1");
            Filme filme1 = new Filme("Filme 1", 2, 10.0);
            Filme filme2 = new Filme("Filme 2", 5, 10.0);

            List<Filme> filmes = Arrays.asList(filme1, filme2);

            // acao
            Locacao locacao = service.alugarFilme(usuario, filmes);
            Double desconto = locacao.getTotalDescontos();

            // verificacao
            assertThat(0.00, is(desconto));
        }

        @Test
        public void deveTerDescontoParaUmFilme() throws FilmeSemEstoqueException, LocadoraException {
            //cenario
            Usuario usuario = new Usuario("Usuario 1");
            Filme filme1 = new Filme("Filme 1", 2, 10.0);
            Filme filme2 = new Filme("Filme 2", 5, 10.0);
            Filme filme3 = new Filme("Filme 3", 5, 10.0);

            List<Filme> filmes = Arrays.asList(filme1, filme2, filme3);

            // acao
            Locacao locacao = service.alugarFilme(usuario, filmes);
            Double desconto = locacao.getTotalDescontos();

            // verificacao
            assertThat(2.5, is(desconto));
        }

        @Test
        public void deveTerDescontoParaDoisFilmes() throws FilmeSemEstoqueException, LocadoraException {
            //cenario
            Usuario usuario = new Usuario("Usuario 1");
            Filme filme1 = new Filme("Filme 1", 2, 10.0);
            Filme filme2 = new Filme("Filme 2", 5, 10.0);
            Filme filme3 = new Filme("Filme 3", 5, 10.0);
            Filme filme4 = new Filme("Filme 4", 5, 10.0);

            List<Filme> filmes = Arrays.asList(filme1, filme2, filme3, filme4);

            // acao
            Locacao locacao = service.alugarFilme(usuario, filmes);
            Double desconto = locacao.getTotalDescontos();

            // verificacao
            assertThat(7.5, is(desconto));
        }

        @Test
        public void deveTerDescontoParaTresFilmes() throws FilmeSemEstoqueException, LocadoraException {
            //cenario
            Usuario usuario = new Usuario("Usuario 1");
            Filme filme1 = new Filme("Filme 1", 2, 10.0);
            Filme filme2 = new Filme("Filme 2", 5, 10.0);
            Filme filme3 = new Filme("Filme 3", 5, 10.0);
            Filme filme4 = new Filme("Filme 4", 5, 10.0);

            List<Filme> filmes = Arrays.asList(filme1, filme2, filme3, filme4);

            // acao
            Locacao locacao = service.alugarFilme(usuario, filmes);
            Double desconto = locacao.getTotalDescontos();

            // verificacao
            assertThat(7.5, is(desconto));
        }

        @Test
        public void deveTerDescontoParaQuatroFilmes() throws FilmeSemEstoqueException, LocadoraException {
            //cenario
            Usuario usuario = new Usuario("Usuario 1");
            Filme filme1 = new Filme("Filme 1", 2, 10.0);
            Filme filme2 = new Filme("Filme 2", 5, 10.0);
            Filme filme3 = new Filme("Filme 3", 5, 10.0);
            Filme filme4 = new Filme("Filme 4", 5, 10.0);
            Filme filme5 = new Filme("Filme 5", 5, 10.0);

            List<Filme> filmes = Arrays.asList(filme1, filme2, filme3, filme4, filme5);

            // acao
            Locacao locacao = service.alugarFilme(usuario, filmes);
            Double desconto = locacao.getTotalDescontos();

            // verificacao
            assertThat(15.00, is(desconto));
        }

        @Test
        public void deveTerDescontoParaCincoFilmes() throws FilmeSemEstoqueException, LocadoraException {
            //cenario
            Usuario usuario = new Usuario("Usuario 1");
            Filme filme1 = new Filme("Filme 1", 2, 10.0);
            Filme filme2 = new Filme("Filme 2", 5, 10.0);
            Filme filme3 = new Filme("Filme 3", 5, 10.0);
            Filme filme4 = new Filme("Filme 4", 5, 10.0);
            Filme filme5 = new Filme("Filme 5", 5, 10.0);
            Filme filme6 = new Filme("Filme 6", 5, 10.0);

            List<Filme> filmes = Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6);

            // acao
            Locacao locacao = service.alugarFilme(usuario, filmes);
            Double desconto = locacao.getTotalDescontos();

            // verificacao
            assertThat(25.00, is(desconto));
        }

        @Test
        public void naoDeveTerDescontoParaMaisDeCincoFilmes() throws FilmeSemEstoqueException, LocadoraException {
            //cenario
            Usuario usuario = new Usuario("Usuario 1");
            Filme filme1 = new Filme("Filme 1", 2, 10.0);
            Filme filme2 = new Filme("Filme 2", 5, 10.0);
            Filme filme3 = new Filme("Filme 3", 5, 10.0);
            Filme filme4 = new Filme("Filme 4", 5, 10.0);
            Filme filme5 = new Filme("Filme 5", 5, 10.0);
            Filme filme6 = new Filme("Filme 6", 5, 10.0);
            Filme filme7 = new Filme("Filme 7", 5, 10.0);

            List<Filme> filmes = Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6, filme7);

            // acao
            Locacao locacao = service.alugarFilme(usuario, filmes);
            Double desconto = locacao.getTotalDescontos();

            // verificacao
            assertThat(25.00, is(desconto));
        }
    */

    @Test
    public void deveAlugarFilmeNoSabadoEDevolderNaSegunda() throws FilmeSemEstoqueException, LocadoraException {
        // cenário
        Usuario usuario = new Usuario("Usuário Xpto");
        Filme filme = new Filme("As tranças da vovó careca", 20, 6.50);

        List<Filme> filmes = Collections.singletonList(filme);

        // ação
        Locacao locacao = service.alugarFilme(usuario, filmes);

        // Verificação
        //boolean isMonday = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
        //assertTrue(isMonday);
        //assertThat(locacao.getDataRetorno(), new DiaSemanaMacher(Calendar.MONDAY) );
        assertThat(locacao.getDataRetorno(), caiEm(Calendar.MONDAY));
        assertThat(locacao.getDataRetorno(), caiNumaSegunda());
    }
}
