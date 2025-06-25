// src/main/java/com/targosystem/varejo/seguranca/domain/service/PasswordEncryptor.java
package com.targosystem.varejo.seguranca.domain.service;

/**
 * Interface para um serviço que realiza a encriptação (hashing) e verificação de senhas.
 * Mantém a lógica de segurança de senhas fora da entidade Usuario.
 */
public interface PasswordEncryptor {
    String encrypt(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}