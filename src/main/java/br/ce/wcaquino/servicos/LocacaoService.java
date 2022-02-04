package br.ce.wcaquino.servicos;

import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.servicos.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.servicos.exception.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

public class LocacaoService {

    private LocacaoDAO dao;
    private SpcServiceImpl spcService;
    private EmailService emailService;

    public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoqueException, LocadoraException {

        if (usuario == null) {
            throw new LocadoraException("Usuario vazio");
        }

        if (spcService.possuiNegativacao(usuario)) {
            throw new LocadoraException("Usuário Negativado!");
        }

        if (filmes == null || filmes.isEmpty()) {
            throw new LocadoraException("Filme vazio");
        }

        Double totalPrecoLocacao = 0d;
        for (Filme filme : filmes) {
            if (filme == null) {
                throw new LocadoraException("Filme vazio");
            }

            if (filme.getEstoque() == 0) {
                throw new FilmeSemEstoqueException("Filme sem estoque");
            }
            totalPrecoLocacao += filme.getPrecoLocacao();
        }

        Locacao locacao = new Locacao();
        locacao.setFilmes(filmes);
        locacao.setUsuario(usuario);
        locacao.setDataLocacao(new Date());
        locacao.setTotalPrecoLocacao(totalPrecoLocacao);
        locacao.setTotalDescontos(getValorTotalDescontos(filmes));
        locacao.setValorPagamento(locacao.getTotalPrecoLocacao() - locacao.getTotalDescontos());

        //Entrega no dia seguinte
        Date dataLocacao = locacao.getDataLocacao();
        locacao.setDataRetorno(dataRetorno(dataLocacao));

        //Salvando a locacao...
        dao.salvar(locacao);

        return locacao;
    }

	public void notificarAtraso() {
		List<Locacao> locacoes = dao.obterLocacoesPendentes();
		for (Locacao locacao : locacoes) {
            if(locacao.getDataRetorno().before(new Date())) {
                emailService.notificarAtraso(locacao.getUsuario());
            }
		}
	}

    private static boolean isDomingo(Date dataEntrega) {
        return DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY);
    }

    private Double getValorTotalDescontos(List<Filme> filmes) {
        double desconto = 0.00;
        for (Filme filme : filmes) {
            desconto += filme.getPrecoLocacao() * getPercentualDesconto(filmes.indexOf(filme));
        }
        return desconto;
    }

    private Double getPercentualDesconto(int posicao) {
        Double[] desconto = {0d, 0d, 25d, 50d, 75d, 100d};
        return posicao > (desconto.length - 1) ? 0 : desconto[posicao] / 100;
    }

    public void setLocaocaoDAO(LocacaoDAO dao) {
        this.dao = dao;
    }

    public void setSpcService(SpcServiceImpl spcService) {
        this.spcService = spcService;
    }

	public void setEmailService(EmailService service) {
		this.emailService = service;
	}

    public static Date dataRetorno(Date param) {
        Date dataRetorno = adicionarDias(param, 1);
        if (isDomingo(dataRetorno)) {
            dataRetorno = adicionarDias(dataRetorno, 1);
        }
        return dataRetorno;
    }
}