package com.lucasdelima.louveapp.domain.repository

/**
 * Interface para o serviço de analytics do app.
 * Define os métodos para todos os eventos de tracking.
 * Esta interface é agnóstica de plataforma e pode ser implementada
 * por diferentes provedores de analytics.
 */
interface AnalyticsService {
    
    /**
     * Registra o login de um usuário
     */
    fun trackUserLogin()
    
    /**
     * Registra o logout de um usuário
     */
    fun trackUserLogout()
    
    /**
     * Registra a visualização de uma tela
     * @param screenName Nome da tela visualizada
     */
    fun trackScreenView(screenName: String)
    
    /**
     * Registra a visualização de um hino
     * @param hymnId ID do hino visualizado
     * @param duration Duração da visualização em segundos
     */
    fun trackHymnViewed(hymnId: String, duration: Int)
    
    /**
     * Registra o compartilhamento do app
     */
    fun trackShareApp()
    
    /**
     * Registra o envio de um ticket de suporte
     */
    fun trackSupportTicketSent()
    
    /**
     * Registra a adição de um hino aos favoritos
     * @param hymnId ID do hino favoritado
     */
    fun trackHymnFavorited(hymnId: String)
    
    /**
     * Registra a remoção de um hino dos favoritos
     * @param hymnId ID do hino removido dos favoritos
     */
    fun trackHymnUnfavorited(hymnId: String)
    
    /**
     * Registra uma busca realizada pelo usuário
     * @param query Termo de busca
     * @param resultCount Número de resultados encontrados
     */
    fun trackSearchPerformed(query: String, resultCount: Int)
}
