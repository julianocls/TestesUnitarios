package br.ce.wcaquino.servicos;

import br.ce.wcaquino.builder.FilmeBuilder;
import br.ce.wcaquino.builder.UsuarioBuilder;
import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static br.ce.wcaquino.builder.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builder.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.macher.MarchersProprios.*;
import static org.hamcrest.CoreMatchers.is;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LocacaoService.class, DataUtils.class})
public class LocacaoServiceTest_PowerMock {

    @InjectMocks
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
        service = PowerMockito.spy(service);
    }

    /**
     * Com ErrorCollector
     */
    @Test
    public void deveAlugarFilme() throws Exception {
        //Locacao locacao = getLocacao();
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Collections.singletonList(FilmeBuilder.umFilme().comValor(5.00).agora());

        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(28, 01, 2022));

        Locacao locacao = service.alugarFilme(usuario, filmes);

        //verificação
        erro.checkThat(locacao.getValorPagamento(), is(5.00));
        //erro.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        erro.checkThat(locacao.getDataLocacao(), eHoje());
        //erro.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
        erro.checkThat(locacao.getDataRetorno(), eHojeComDiferencaDias(1));
    }

    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
        //Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        //cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29, 01, 2022));

        //acao
        Locacao retorno = service.alugarFilme(usuario, filmes);

        //verificacao
        erro.checkThat(retorno.getDataRetorno(), caiNumaSegunda());
        //assertThat(retorno.getDataRetorno(), caiNumaSegunda());

        PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
    }


}
