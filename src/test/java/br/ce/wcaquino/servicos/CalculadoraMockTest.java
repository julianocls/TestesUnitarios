package br.ce.wcaquino.servicos;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class CalculadoraMockTest {

    @Mock
    private Calculadora calcMock;

    @Spy
    private Calculadora calcSpy;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void haveShowDifferenceBetweenMockAndSpy() {
        Mockito.when(calcMock.somar(1, 2)).thenReturn(5);
        //Mockito.when(calcSpy.somar(1, 2)).thenReturn(5);
        Mockito.doReturn(5).when(calcSpy).somar(1, 2);
        Mockito.doNothing().when(calcSpy).imprime();

        System.out.println("Mock: " + calcMock.somar(1, 2));
        System.out.println("Spy: " + calcSpy.somar(1, 2));

        System.out.println("Mock: " + calcMock.somar(1, 3));
        System.out.println("Spy: " + calcSpy.somar(1, 3));

        System.out.println("Mock");
        calcMock.imprime();
        System.out.println("Spy");
    }

}
