package br.ce.wcaquino.servicos;

import br.ce.wcaquino.runners.ParallelRunner.ParallelRunner;
import br.ce.wcaquino.servicos.exception.NaoPodeDividirPorZeroException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

//@RunWith(ParallelRunner.class) usando maven
public class CalculadoraTest {

    private Calculadora calc;

    @Before
    public void setup() {
        calc = new Calculadora();
        System.out.println("Beginner....");
    }

    @After
    public void tearDown() {
        System.out.println("Finishing...");
    }

    @Test
    public void test() {
        Calculadora calc = Mockito.mock(Calculadora.class);

        ArgumentCaptor<Integer> argCpt = ArgumentCaptor.forClass(Integer.class);
        Mockito.when(calc.somar(argCpt.capture(), argCpt.capture())).thenReturn(5);

        Assert.assertEquals(5, calc.somar(1, 564));

        System.out.println(argCpt.getAllValues());
    }

    @Test
    public void deveSomarDoisNumeros() {
        // cenario
        int a = 5;
        int b = 2;

        // acao
        int resultado = calc.somar(a, b);

        // verificação
        assertEquals(7, resultado);
    }

    @Test
    public void deveSubtrairDoisNumeros() {
        // cenario
        int a = 5;
        int b = 2;

        // acao
        int resultado = calc.subtrair(a, b);

        // verificacao
        assertEquals(3, resultado);
    }

    @Test
    public void deveDividirDoisNumeros() throws NaoPodeDividirPorZeroException {
        // cenario
        int a = 8;
        int b = 2;

        // acao
        int resultado = calc.dividir(a, b);

        // verificação
        assertEquals(4, resultado);
    }

    @Test(expected = NaoPodeDividirPorZeroException.class)
    public void deveLancarNaoPodeDividirPorZeroExceptionAoDividirPorZero() throws NaoPodeDividirPorZeroException {
        // cenario
        int a = 8;
        int b = 0;

        // acao
        int resultado = calc.dividir(a, b);

        // verificacao
        fail("Não pode dividir por zero!");
    }

}
