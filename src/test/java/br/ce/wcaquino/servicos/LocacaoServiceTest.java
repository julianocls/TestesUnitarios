package br.ce.wcaquino.servicos;

import br.ce.wcaquino.builder.FilmeBuilder;
import br.ce.wcaquino.builder.LocacaoBuilder;
import br.ce.wcaquino.builder.UsuarioBuilder;
import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.servicos.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.servicos.exception.LocadoraException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static br.ce.wcaquino.builder.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builder.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.macher.MarchersProprios.*;
import static br.ce.wcaquino.utils.DataUtils.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class LocacaoServiceTest {

    @InjectMocks
    @Spy
    private LocacaoService service;

    @Mock
    private SpcServiceImpl spcService;
    @Mock
    private LocacaoDAO dao;
    @Mock
    private EmailService emailService;

    @Rule
    public ErrorCollector erro = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
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
        //Locacao locacao = getLocacao();
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Collections.singletonList(FilmeBuilder.umFilme().comValor(5.00).agora());

        Mockito.doReturn(obterData(28, 4, 2017)).when(service).obterData();

        Locacao locacao = service.alugarFilme(usuario, filmes);

        //verificação
        erro.checkThat(locacao.getValorPagamento(), is(5.00));
        erro.checkThat(isMesmaData(locacao.getDataLocacao(), obterData(28, 04, 2017)), is(true));
        erro.checkThat(isMesmaData(locacao.getDataRetorno(), obterData(29, 04, 2017)), is(true));
    }

    private Locacao getLocacao() throws Exception {
        //cenario
        Usuario usuario = umUsuario().agora();
        Filme filme1 = umFilme().comValor(5.0).agora(); //new Filme("Filme 1", 2, 5.0);
        Filme filme2 = umFilme().comValor(25.0).agora(); //new Filme("Filme 2", 5, 25.0);

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
        Usuario usuario = umUsuario().comNome("Jose").agora();
        Filme filme1 = umFilme().semEstoque().comValor(5.0).agora(); //new Filme("Filme 1", 0, 5.0);
        Filme filme2 = umFilme().semEstoque().comValor(25.0).agora(); //new Filme("Filme 2", 0, 25.0);

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
        Usuario usuario = umUsuario().agora();
        Filme filme1 = umFilme().semEstoque().agora(); //new Filme("Filme 1", 0, 5.0);
        Filme filme2 = umFilme().semEstoque().agora(); //new Filme("Filme 2", 0, 25.0);

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
        Usuario usuario = umUsuario().agora();
        Filme filme1 = umFilme().semEstoque().agora(); //new Filme("Filme 1", 0, 5.0);
        Filme filme2 = umFilme().semEstoque().agora(); //new Filme("Filme 2", 0, 25.0);

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
    public void naoDeveAlugarFilmeSemUsuario() throws Exception {
        //cenario
        Usuario usuario = null; //UsuarioBuilder.usuario().get();
        Filme filme1 = umFilme().agora(); // new Filme("Filme 1", 10, 5.0);
        Filme filme2 = umFilme().agora(); //new Filme("Filme 2", 30, 25.0);

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
    public void naoDeveAlugarFilmeSemFilme() throws Exception {
        //cenario
        Usuario usuario = umUsuario().agora();
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

    /*@Test
    public void deveAlugarFilmeNoSabadoEDevolderNaSegunda() throws FilmeSemEstoqueException, LocadoraException {
        // cenário
        Usuario usuario = umUsuario().agora();
        Filme filme = umFilme().agora(); //new Filme("As tranças da vovó careca", 20, 6.50);
        List<Filme> filmes = Collections.singletonList(filme);

        LocalDate localDate = LocalDate.of(2022, Month.JANUARY, 29); // sabado
        Date dataLocacao = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        //Mockito.when()

        // ação
        Locacao locacao = LocacaoBuilder.umaLocacao().comDataLocacao(dataLocacao).agora();
        //Locacao locacao = service.alugarFilme(usuario, filmes);

        // Verificação
        //boolean isMonday = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
        //assertTrue(isMonday);
        //assertThat(locacao.getDataRetorno(), new DiaSemanaMacher(Calendar.MONDAY) );
        assertThat(locacao.getDataRetorno(), caiEm(Calendar.MONDAY));
        assertThat(locacao.getDataRetorno(), caiNumaSegunda());
    }*/

    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
        //Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        //cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        Mockito.doReturn(obterData(29, 4, 2017)).when(service).obterData();

        //acao
        Locacao retorno = service.alugarFilme(usuario, filmes);

        //verificacao
        erro.checkThat(retorno.getDataRetorno(), caiNumaSegunda());
        //assertThat(retorno.getDataRetorno(), caiNumaSegunda());
    }

    /*
    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabadoStatic() throws Exception {
        //Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        //cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2022);
        calendar.set(Calendar.MONTH, 01);
        calendar.set(Calendar.DAY_OF_MONTH, 29);
        PowerMockito.mockStatic(Calendar.class);
        PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);

        //acao
        Locacao retorno = service.alugarFilme(usuario, filmes);

        //verificacao
        erro.checkThat(retorno.getDataRetorno(), caiNumaSegunda());
        //assertThat(retorno.getDataRetorno(), caiNumaSegunda());
        //PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();

        PowerMockito.verifyStatic(Mockito.times(3));
        Calendar.getInstance();
    }
*/
    @Test
    public void naoDeveAlugarParaNegativado() throws Exception {
        // cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Collections.singletonList(FilmeBuilder.umFilme().agora());

        //Mockito.when(spcService.possuiNegativacao(usuario)).thenReturn(true); sem matchers
        Mockito.when(spcService.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true); // com metchers

        // ação
        //exception.expect(LocadoraException.class);
        //exception.expectMessage("Usuário Negativado!");
        // ou da forma abaixo

        try {
            service.alugarFilme(usuario, filmes);

            // Verificação
            Assert.fail();
        } catch (LocadoraException e) {
            Assert.assertThat(e.getMessage(), is("Usuário Negativado!"));
        }

        Mockito.verify(spcService).possuiNegativacao(usuario);

    }

    /**
     * Verificar o comportamento do metodo
     */
    @Test
    public void deveEnviarEmailParaLocacoesAtrasadas() {
        Usuario usuario1 = UsuarioBuilder.umUsuario().agora();
        Usuario usuario2 = UsuarioBuilder.umUsuario().comNome("Usuario em dia").agora();
        Usuario usuario3 = UsuarioBuilder.umUsuario().comNome("Usuario com atraso").agora();

        // cenario
        List<Locacao> locacoes = Arrays.asList(
                LocacaoBuilder.umaLocacao().comAtraso().comUsuario(usuario1).agora(),
                LocacaoBuilder.umaLocacao().comUsuario(usuario2).agora(),
                LocacaoBuilder.umaLocacao().comAtraso().comUsuario(usuario3).agora(),
                LocacaoBuilder.umaLocacao().comAtraso().comUsuario(usuario3).agora()
        );

        Mockito.when(dao.obterLocacoesPendentes()).thenReturn(locacoes);

        // acao
        service.notificarAtraso();

        // verificacao
        Mockito.verify(emailService, Mockito.times(3)).notificarAtraso(Mockito.any(Usuario.class)); // Garante numero de invocacoes
        Mockito.verify(emailService).notificarAtraso(usuario1);
        Mockito.verify(emailService, Mockito.atLeastOnce()).notificarAtraso(usuario3); // enviou no minimo 1 email
        Mockito.verify(emailService, Mockito.never()).notificarAtraso(usuario2); // verificar que nao recebeu (never ega o verify)
        Mockito.verifyNoMoreInteractions(emailService); // quantidade de chamadas no emailService
    }

    @Test
    public void deveTratarErroNoSPC() throws Exception {
        // cenario
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Collections.singletonList(FilmeBuilder.umFilme().agora());

        Mockito.when(spcService.possuiNegativacao(usuario)).thenThrow(new Exception("Erro catastrófico"));

        // verificacao
        exception.expect(LocadoraException.class);
        exception.expectMessage("Erro na comunicação com o SPC, tente novamente!");

        // acao
        service.alugarFilme(usuario, filmes);
    }

    @Test
    public void deveProrrogarUmaLocacao() {
        // cenário
        Locacao locacao = LocacaoBuilder.umaLocacao().agora();

        // ação
        service.prorrogarLocacao(locacao, 3);

        ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
        Mockito.verify(dao).salvar(argCapt.capture());
        Locacao locacaoRetornada = argCapt.getValue();

        // verificação
        erro.checkThat(locacaoRetornada.getValorPagamento(), is(10.00));
        erro.checkThat(locacaoRetornada.getDataLocacao(), eHoje());
        erro.checkThat(locacaoRetornada.getDataRetorno(), eHojeComDiferencaDias(3));
    }

    @Test
    public void deveCalcularValoDesconto() throws Exception {
        // cenário
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        //List<Filme> filmes = Collections.singletonList(FilmeBuilder.umFilme().agora());
        Filme filme1 = FilmeBuilder.umFilme().agora();
        Filme filme2 = FilmeBuilder.umFilme().agora();
        Filme filme3 = FilmeBuilder.umFilme().agora();
        List<Filme> filmes = Arrays.asList(filme1, filme2, filme3);

        // ação
        Class<LocacaoService> clazz = LocacaoService.class;
        Method method = clazz.getDeclaredMethod("getValorTotalDescontos", List.class);
        method.setAccessible(true);
        Double valorDescontos = (Double) method.invoke(service, filmes);

        // Verificação
        erro.checkThat(valorDescontos, is(2.50));

    }
}
