Ce projet a été réalisé par :

SANCHEZ Nicolas
FATEH Nacer

Compilation et exécution :

Exécuter la classe MicroblogClient en indiquant le nom d'utilisateur puis choisir le mode souhaité à l'aide du menu affiché (
1: publish,
2: subscribe,
3: unsubscribe,
4: EXIT)

# Protocole microblogamu

L'application est une application de microblogage (ou gazouillage). Les utilisateurs peuvent poster des courts messages, lire les derniers messages postés, s'abonner à d'autres utilisateurs (auquel cas ils reçoivent les messages de ces utilisateurs) et s'en désabonner. Les messages peuvent contenir des mots-clés (tags), et l'on peut consulter les messages récents contenant un mot-clé donné, ainsi que s'abonner/se désabonner à des/de mots-clés.

Les utilisateurs sont identifiés par un nom : suite de caractères alphanumériques sans espace commençant par @ (e.g., @alice, @B42r115). Un mots-clé est une suite de caractères alphanumériques sans espace commençant par # (hashtag).

Les messages de l'application ont chacun un identifiant unique id sur 64 bits. Cet identifiant est attribué par le serveur. Le contenu d'un message est limité à 256 caractères.

Le port par défaut de microblogamu est 12345.

# Requêtes/réponses
L'interaction avec le serveur s'effectue soit en mode non-connecté par des échanges de type requête/réponse, soit par en mode connecté (voir plus bas). Chaque requête donne lieu à l'ouverture d'une nouvelle connexion, qui est fermée une fois la réponse obtenue. Une requête ou une réponse se compose d'un entête (header) et d'un corps (body). L'entête permet d'identifier la requête et contient ses paramètres tandis le que le corps est formé de son contenu (qui peut-être vide). L'entête et le corps sont terminés par une retour chariot (carriage return) suivi de fin de ligne (linefeed), e.g. \r\n. Si le corps est vide, la requête ou réponse se termine donc par \r\n\r\n.

Typiquement, une connexion est établie à l'initiative du client, qui ensuite envoie la requête. La connexion est fermée une fois la réponse transmise (coté serveur)/reçue (coté client).

## Requêtes

### Publier un message
entête : PUBLISH author:@user
corps : contenu du message
réponse :OK ou ERROR
@user est l'auteur du message. Le serveur renvoie une réponse OK ou ERROR décrite plus bas.

### Recevoir des identifiants de messages
entête : RCV_IDS [author:@user] [tag:#tag] [since_id:id] [limit:n]
corps: vide
réponse: le serveur renvoie une réponse MSG_IDS contenant les identifiants des n messages les plus récents correspondant aux critères de la requête. Les identifiants sont ordonnés par ordre antichronologiques (les plus récents en premier).
(optionnel) author:@user l'auteur des messages est @user
(optionnel) tag:#tag les messages contiennent le mot clé #tag
(optionnel) since_id:id les messages ont été publiés après le message dont l'identifiant est id
(optionnel) limit:n aux plus n identifiants sont renvoyés. La valeur par défaut de n est n=5.
Ainsi, la requêtes RCV_IDS déclenchera une réponse MSG_IDS contenant les identifiants des 5 derniers messages publiés, les identifiants des messages les plus récents en premier.

### Recevoir un message
entête : RCV_MSG msg_id:id
corps : vide
réponse : le serveur renvoie une réponse MSG contenant le message dont l'identifiant est id ou ERROR si il n'existe pas de message dont l'identifiant est id.

### Publier un message en réponse à un autre
entête : REPLY author:@user reply_to_id:id
corps : contenu du message
réponse :OK ou ERROR
Le message est publié en réponse au message dont l'identifiant est id Le serveur renvoie une réponse OK ou ERROR.

### Re-publier un message
entête : REPUBLISH author:@user msg_id:id
corps : vide
réponse :OK ou ERROR
Le message dont l'identifiant est id est publié de nouveau.

## Réponses

Ces réponses sont émises par le serveur en retour d'une requête

### Confirmation de publication
entête :OK
corps: vide renvoyée suite à une requête de publication d'un message

### Signalement d'une erreur
entête : ERROR
corps : message d'erreur
Exemples de messages d'erreur : Bad request format, Unknown message id, etc .

### Liste d'identifiants de messages :
entête : MSG_IDS
corps : identifiants de messages, un par ligne, ordonnées par les plus récents en premier.
Message

entête : MSG author:@user msg_id:id [reply_to_id:id] [republished:true/false]. L'entête contient les méta-données du message, i.e. le nom de l'auteur, son identifiant, s'il s'agit d'un republication ou d'une réponse à un autre message. Les couples key:value sont séparés par un ou plusieurs espaces. Les couples entre crochet [ ] sont optionnels.
corps : le contenu du message.
Mode flux
Le mode requête/réponse requiert l'établissement d'une nouvelle connexion pour la réception de chaque nouveau message. De plus le client a la charge de périodiquement de demander au serveur les messages qui l'intéressent. Dans le mode flux, le serveur notifie le client à chaque fois qu'un nouveau message pour lequel le client a manifesté de l'intérêt est publié.

## Se connecter
entête : CONNECT user:@user
corps : vide
réponse : OK ou ERROR.
Cette requête déclenche l'ouverture d'une connexion avec le serveur sur laquelle sera transmis le flux de messages auquel s'abonne le client. La gestion des abonnements se fait au moyen des requêtes SUBSCRIBE et UNSUBSCRIBE.

## S'abonner
entête : SUBSCRIBE author:@user et SUBSCRIBE tag:#tag
corps : vide
réponse : OK ou ERROR. Le serveur envoie ERROR si @user n'est pas géré par l'application. Si par contre le mot-clé #tag n'est pas encore géré, il est ajouté et OK est renvoyé.
Suite à cette requête, le serveur enverra une réponse MSG sur la connexion précédemment ouverte par CONNECT à chaque fois qu'un message dont l'auteur est @user ou qui contient le mot-clé #tag est publié.

## Se désabonner
entête : UNSUBSCRIBE author:@user et UNSUBSCRIBE tag:#tag
corps : vide
réponse : OKou ERROR. Le serveur renvoie ERROR si le client n'est pas abonné à @user ou au mot-clé #tag.
Suite à cette requête, le serveur cesse d'envoyer des messages dont l'auteur est @user ou qui contiennent le mot-clé #tag.

## Serveur centralisé
On commencera par un service centralisé correspondant à un seul serveur.

# Serveur rudimentaire
Écrire un serveur rudimentaire qui répond aux requêtes PUBLISH. Les messages reçues via les requête PUBLISH seront affichés avec leur identifiant sur la sortie standard.
Écrire un premier client publisher qui :
Demande un pseudo (@user) à l'utilisateur
Attend les messages de l'utilisateur (sur l'entrée standard)
Transmet les messages de l'utilisateur au serveur
Valider votre client et votre serveur avec netcat.

# Serveur non-connecté complet
Compléter votre serveur pour répondre aux requêtes RCV_IDS et RCV_MSG
Écrire un deuxième client follower qui
Récupère les identifiants des messages d'un ou plusieurs utilisateurs (spécifiés au lancement du client)
Affiche le contenu des messages correspondant à ces identifiants.
Vous avez le choix de l'architecture du serveur : utilisation de select, d'un pool de threads ou combinaison des deux. On fera attention à l'unicité des identifiants des messages, ainsi qu'à l'ordre dans lequel les messages sont affichés.

Finaliser votre serveur en ajoutant la gestion des requêtes REPLY et REPUBLISH.
Écrire un client repost qui re-publie chaque message émis par un ensemble d'utilisateurs déterminé au lancement du client.
Valider votre serveur avec plusieurs clients.
Gestion des flux
Nous allons maintenant écrire le serveur MicroblogCentral, qui en plus en plus du mode requête/réponse supportera la gestion des flux d'abonnement.

Proposer une architecture fondée sur des files d'attente associées à chaque client abonné à des mots-clés et/ou utilisateurs.
On pourra utiliser ces files en mode producteur/consommateur : * Les producteurs placent dans les files concernées les nouveaux messages publiés. * Il y a un seul consommateur par file, qui renvoie dans la socket du client les messages présents dans la file d'attente associée à ce client.

On pourra utiliser les implémentations ArrayBlockingQueue<T> ou ConcurrentLinkedQueue<T>.

Comment s'assurer que la communication est asynchrone ?

Proposer une validation fonctionnelle basée sur des tests automatisés avec plusieurs clients.

Écrire le serveur MicroblogCentral.

Écrire un client complet MicroblogClient. Il lira sur l'entrée standard les commandes de l'utilisateur (PUBLISH, REPLY, REPUBLISH, (UN)SUBSCRIBE) avec leur entrée. Il affichera les messages reçus précédés de leur auteur et de leur identifiant.
