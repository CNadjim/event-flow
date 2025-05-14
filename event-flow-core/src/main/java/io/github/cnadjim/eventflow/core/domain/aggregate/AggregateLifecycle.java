package io.github.cnadjim.eventflow.core.domain.aggregate;

import lombok.extern.slf4j.Slf4j;

/**
 * Utilitaire statique pour gérer le cycle de vie des agrégats
 */
@Slf4j
public class AggregateLifecycle {

    private static final ThreadLocal<Boolean> DELETED_MARKER = new ThreadLocal<>();

    /**
     * Marque l'agrégat courant comme devant être supprimé.
     * Cette méthode doit être appelée dans une méthode annotée avec @EventSourcingHandler.
     */
    public static void markDeleted() {
        DELETED_MARKER.set(true);
    }

    /**
     * Vérifie si l'agrégat courant a été marqué pour suppression
     * @return true si l'agrégat est marqué pour suppression
     */
    public static boolean isMarkedDeleted() {
        return Boolean.TRUE.equals(DELETED_MARKER.get());
    }

    /**
     * Réinitialise l'état de marquage de suppression
     */
    public static void resetDeletedMarker() {
        DELETED_MARKER.remove();
    }
}
