package com.gimnasio.banco.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.gimnasio.banco.application.usecase.CreateAccountUseCase;
import com.gimnasio.banco.application.usecase.GetBalanceUseCase;
import com.gimnasio.banco.application.usecase.TransferMoneyUseCase;
import com.gimnasio.banco.domain.model.TransferService;
import com.gimnasio.banco.domain.repository.AccountRepository;

/**
 * CONFIGURACIÓN DE BEANS
 * 
 * Registra los Use Cases y Domain Services como beans de Spring.
 * 
 * Nota: Lombok @RequiredArgsConstructor se encarga de la inyección,
 * aquí solo creamos las instancias.
 */
@Configuration
@EnableTransactionManagement
public class BeanConfiguration {

    @Bean
    public TransferService transferService() {
        return new TransferService();
    }

    @Bean
    public CreateAccountUseCase createAccountUseCase(AccountRepository accountRepository) {
        return new CreateAccountUseCase(accountRepository);
    }

    @Bean
    public TransferMoneyUseCase transferMoneyUseCase(
            AccountRepository accountRepository,
            TransferService transferService,
            TransferMoneyUseCase.NotificationPort notificationPort) {
        return new TransferMoneyUseCase(accountRepository, transferService, notificationPort);
    }

    @Bean
    public GetBalanceUseCase getBalanceUseCase(AccountRepository accountRepository) {
        return new GetBalanceUseCase(accountRepository);
    }
}
