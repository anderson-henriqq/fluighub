package com.fluig.test.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.fluig.model.CryptoModel;
import com.fluig.model.ResponseGeneralModel;
import com.fluig.service.CryptoService;
import com.fluig.utils.Crypto;

public class CryptoServiceTest {

    private CryptoService cryptoService;
    private final String TEST_PASSPHRASE = "senhaSecreta";
    private final String ENCRYPTED_TEXT = "textoEncriptado";
    private final String DECRYPTED_TEXT = "textoDecriptado";

    @Before
    public void setUp() {
        // Inicializa a classe que será testada
        cryptoService = new CryptoService();
    }

    @Test
    public void testExecuteService_Crypto() throws NoSuchPaddingException, IllegalBlockSizeException, 
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        CryptoModel request = new CryptoModel();
        request.setEndpoint("crypto");
        request.setPassphrase(TEST_PASSPHRASE);
        
        try (MockedStatic<Crypto> cryptoMock = mockStatic(Crypto.class)) {
            cryptoMock.when(() -> Crypto.crypt(TEST_PASSPHRASE)).thenReturn(ENCRYPTED_TEXT);
            
            ResponseGeneralModel response = cryptoService.executeService(request);
            
            assertNotNull("Resposta não deve ser nula", response);
            assertEquals("Mensagem deve ser o texto encriptado", ENCRYPTED_TEXT, response.getMessage());
            assertEquals("Status code deve ser 200", 200, response.getCode());
            assertFalse("Erro deve ser falso", response.isError());
            
            cryptoMock.verify(() -> Crypto.crypt(TEST_PASSPHRASE));
        }
    }

    @Test
    public void testExecuteService_Decrypto() throws NoSuchPaddingException, IllegalBlockSizeException, 
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Preparação
        CryptoModel request = new CryptoModel();
        request.setEndpoint("decrypto");
        request.setPassphrase(TEST_PASSPHRASE);
        
        try (MockedStatic<Crypto> cryptoMock = mockStatic(Crypto.class)) {
            // Configura o comportamento esperado do método estático
            cryptoMock.when(() -> Crypto.decrypt(TEST_PASSPHRASE)).thenReturn(DECRYPTED_TEXT);
            
            // Execução
            ResponseGeneralModel response = cryptoService.executeService(request);
            
            // Verificação
            assertNotNull("Resposta não deve ser nula", response);
            assertEquals("Mensagem deve ser o texto decriptado", DECRYPTED_TEXT, response.getMessage());
            assertEquals("Status code deve ser 200", 200, response.getCode());
            assertFalse("Erro deve ser falso", response.isError());
            
            // Verifica se o método estático foi chamado com o parâmetro correto
            cryptoMock.verify(() -> Crypto.decrypt(TEST_PASSPHRASE));
        }
    }
    
    @Test
    public void testExecuteService_CaseInsensitive() throws NoSuchPaddingException, IllegalBlockSizeException, 
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Preparação - usamos maiúsculas no endpoint para testar case insensitive
        CryptoModel request = new CryptoModel();
        request.setEndpoint("CRYPTO");
        request.setPassphrase(TEST_PASSPHRASE);
        
        try (MockedStatic<Crypto> cryptoMock = mockStatic(Crypto.class)) {
            // Configura o comportamento esperado do método estático
            cryptoMock.when(() -> Crypto.crypt(TEST_PASSPHRASE)).thenReturn(ENCRYPTED_TEXT);
            
            // Execução
            ResponseGeneralModel response = cryptoService.executeService(request);
            
            // Verificação
            assertEquals("Mensagem deve ser o texto encriptado", ENCRYPTED_TEXT, response.getMessage());
            
            // Verifica se o método estático foi chamado com o parâmetro correto
            cryptoMock.verify(() -> Crypto.crypt(TEST_PASSPHRASE));
        }
    }
    
    @Test
    public void testExecuteService_UnknownEndpoint() throws NoSuchPaddingException, IllegalBlockSizeException, 
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Preparação - usando um endpoint desconhecido
        CryptoModel request = new CryptoModel();
        request.setEndpoint("unknown");
        request.setPassphrase(TEST_PASSPHRASE);
        
        try (MockedStatic<Crypto> cryptoMock = mockStatic(Crypto.class)) {
            // Execução
            ResponseGeneralModel response = cryptoService.executeService(request);
            
            // Verificação
            assertNotNull("Resposta não deve ser nula", response);
            assertEquals("Mensagem deve ser vazia", "", response.getMessage());
            assertEquals("Status code deve ser 200", 200, response.getCode());
            assertFalse("Erro deve ser falso", response.isError());
            
            // Verifica que nenhum método estático foi chamado
            cryptoMock.verify(() -> Crypto.crypt(anyString()), never());
            cryptoMock.verify(() -> Crypto.decrypt(anyString()), never());
        }
    }
}