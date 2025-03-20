## EventFlow

Event-flow est un framework Java conçu pour implémenter des architectures orientées événements et le pattern CQRS (Command Query Responsibility Segregation).
Il offre une approche hexagonale pour construire des applications basées sur l'évent sourcing.

## Composants principaux

**Objets de domaine essentiels**:

- `Event`: Représente un événement qui s'est produit dans le système
- `Command`: Représente une action à effectuer
- `Query`: Représente une demande d'information
- `Aggregate`: Entité de domaine avec un état qui peut être modifié par des événements

**Handlers**:

- `CommandHandler`: Traite les commandes et produit des événements
- `EventHandler`: Réagit aux événements
- `QueryHandler`: Traite les requêtes et renvoie des résultats
- `EventSourcingHandler`: Applique les événements aux agrégats pour reconstruire leur état

**Services de passerelle**:

- `CommandGateway`: Envoie les commandes à leurs gestionnaires
- `EventGateway`: Envoie les événements à leurs gestionnaires
- `QueryGateway`: Envoie les requêtes et retourne les réponses

**Interfaces d'infrastructure**:

- `EventStore`: Persiste les événements
- `EventPublisher`: Publie des événements
- `EventSubscriber`: S'abonne aux sujets d'événements
- `HandlerRegistry`: Enregistre et gère les gestionnaires


## Options d'implémentation

Event-flow propose plusieurs options d'implémentation :

- Implémentations en mémoire pour les tests (`InMemoryEventStore`, `InMemoryHandlerRegistry`)
- Intégration avec MongoDB pour le stockage d'événements
- Intégration avec Kafka pour la publication/souscription d'événements


## Intégration avec Spring

Le framework s'intègre bien avec l'écosystème Spring:

- Auto-configuration Spring Boot
- Intégration Spring MongoDB
- Intégration Spring Kafka


## Configuration par annotations

Event-flow utilise fortement les annotations pour simplifier la configuration:

- `@Aggregate`: Marque une classe comme agrégat
- `@AggregateId`: Identifie le champ ID d'un agrégat
- `@HandleCommand`: Marque une méthode comme gestionnaire de commande
- `@HandleEvent`: Marque une méthode comme gestionnaire d'événement
- `@HandleQuery`: Marque une méthode comme gestionnaire de requête
- `@ApplyEvent`: Marque une méthode qui applique un événement à un agrégat


## Architecture

L'architecture d'event-flow suit les patterns CQRS et Event Sourcing :

1. **Côté Commande**: Les commandes sont envoyées aux gestionnaires qui produisent des événements, lesquels sont stockés et publiés
2. **Côté Requête**: Les requêtes sont envoyées aux gestionnaires qui retournent des données depuis les modèles de lecture
3. **Event Sourcing**: L'état des agrégats est reconstruit en rejouant les événements, qui constituent la source de vérité du système

Le framework inclut également des applications d'exemple démontrant son utilisation dans des contextes réels comme la gestion de comptes utilisateurs avec des API RESTful.
