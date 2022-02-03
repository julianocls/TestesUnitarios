package br.ce.wcaquino.entidades;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Locacao {

	private Usuario usuario;
	private List<Filme> filmes = new ArrayList<Filme>();
	private Date dataLocacao;
	private Date dataRetorno;
	private Double valorPagamento;
	private Double totalDescontos;
	private Double totalPrecoLocacao;

	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public Date getDataLocacao() {
		return dataLocacao;
	}
	public void setDataLocacao(Date dataLocacao) {
		this.dataLocacao = dataLocacao;
	}
	public Date getDataRetorno() {
		return dataRetorno;
	}
	public void setDataRetorno(Date dataRetorno) {
		this.dataRetorno = dataRetorno;
	}
	public Double getValorPagamento() {
		return valorPagamento;
	}
	public void setValorPagamento(Double valor) {
		this.valorPagamento = valor;
	}
	public List<Filme> getFilmes() {return filmes;}
	public void setFilmes(List<Filme> filmes) {	this.filmes = filmes;}
	public Double getTotalDescontos() {return totalDescontos;}
	public void setTotalDescontos(Double totalDescontos) {this.totalDescontos = totalDescontos;}
	public Double getTotalPrecoLocacao() {return totalPrecoLocacao;}
	public void setTotalPrecoLocacao(Double totalPrecoLocacao) {this.totalPrecoLocacao = totalPrecoLocacao;}
}