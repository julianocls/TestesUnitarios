package br.ce.wcaquino.builder;

import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.servicos.LocacaoService;
import br.ce.wcaquino.utils.DataUtils;

import java.util.Arrays;

import java.lang.Double;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;

import static br.ce.wcaquino.builder.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builder.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.utils.DataUtils.adicionarDias;


public class LocacaoBuilder {
	private Locacao locacao;
	private LocacaoBuilder(){}

	public static LocacaoBuilder umaLocacao() {
		LocacaoBuilder builder = new LocacaoBuilder();
		inicializarDadosPadroes(builder);
		return builder;
	}

	public static void inicializarDadosPadroes(LocacaoBuilder builder) {
		builder.locacao = new Locacao();
		Locacao locacao = builder.locacao;

		locacao.setUsuario(umUsuario().agora());
		locacao.setFilmes(Collections.singletonList(umFilme().agora()));

		locacao.setDataLocacao(new Date());
		locacao.setDataRetorno(LocacaoService.dataRetorno(locacao.getDataLocacao()));

		locacao.setTotalPrecoLocacao(10.00);
		locacao.setTotalDescontos(0.00);
		locacao.setValorPagamento(locacao.getTotalPrecoLocacao() - locacao.getTotalDescontos());
	}

	public LocacaoBuilder comUsuario(Usuario param) {
		locacao.setUsuario(param);
		return this;
	}

	public LocacaoBuilder comListaFilmes(Filme... params) {
		locacao.setFilmes(Arrays.asList(params));
		return this;
	}

	public LocacaoBuilder comDataLocacao(Date param) {
		locacao.setDataLocacao(param);
		locacao.setDataRetorno(LocacaoService.dataRetorno(param));
		return this;
	}


	public LocacaoBuilder comDataRetorno(Date param) {
		locacao.setDataRetorno(param);
		return this;
	}

	public LocacaoBuilder comTotalPrecoLocacao(Double param) {
		locacao.setTotalPrecoLocacao(param);
		locacao.setValorPagamento(locacao.getTotalPrecoLocacao() - locacao.getTotalDescontos());
		return this;
	}

	public LocacaoBuilder comTotalDesconto(Double desconto) {
		locacao.setTotalDescontos(desconto);
		locacao.setValorPagamento(locacao.getTotalPrecoLocacao() - locacao.getTotalDescontos());
		return this;
	}

	public Locacao agora() {
		return locacao;
	}	

}
