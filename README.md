#  Description:
LibraryFX est une application de gestion de bibliothèque moderne développée avec Spring Boot et JavaFX. Elle permet de gérer efficacement les livres, les adhérents, les emprunts et les retards avec une interface graphique intuitive.

# **🎯 Fonctionnalités Principales:**

   ✅ Gestion des Livres - Ajout, modification, suppression et recherche de livres 

   👥 Gestion des Adhérents - Inscription, modification et suivi des adhérents

   📋 Gestion des Emprunts - Création, suivi et retour des emprunts

   ⏰ Détection Automatique des Retards - Calcul automatique des amendes

   📧 Notifications par Email - Rappels automatiques pour les retours

   📊 Tableau de Bord Statistiques - Vue d'ensemble en temps réel

   🔍 Recherche Avancée - Par titre, auteur, catégorie ou adhérent

   🎨 Interface Moderne - Design Material inspiré

  # Stack Technologique

  * **Backend**: *Spring Boot 3.4.1+, Spring Data JPA, Hibernate*
  
  * **Frontend**: *JavaFX 17.0.6+, FXML, CSS*
  
  * **Base de données**: *PostgreSQL 16+*
  
  * **Sécurité**: *BCrypt pour le hashing des mots de passe*
  
  * **Build**: *Maven 3.9+*
  
  * **Java**: *Version 17+*

 # Configuration: 
  Modifier *src/main/resources/application.properties* **(changer *application.properties.example*)** :
  
  -Base de données
  
      spring.datasource.url=jdbc:postgresql://localhost:5432/libraryfx_db

      spring.datasource.username=postgres

      spring.datasource.password=votre_mot_de_passe

   -Email (optionnel - pour les notifications)

      spring.mail.username=votre-email@gmail.com
      
      spring.mail.password=votre-app-password
      
  Identifiants par défaut **(*src/main/java/config/DataInitializer.java*)** :

    Email : admin@library.com

    Mot de passe : admin123        

# Règles de Gestion

  **Emprunt de Livres**

  * *Un livre ne peut être emprunté que s'il est disponible*
  
  * *Durée par défaut : 2 semaines*
  
  * *Un adhérent peut emprunter plusieurs livres*


  **Retour de Livre**

  * *Le retour remet le livre à "disponible"*
  
  * *Calcul automatique du retard*
  
  * *Génération d'amende si retard*
  
  **Prolongation**
  
  * *Possible uniquement si le livre n'est pas en retard*
  
  * *Extension de 7 jours maximum*
    
  **Adhérent**
  
  * *Email unique obligatoire*
  
  * *Peut être désactivé (soft delete)*
  
  * *Conservation de l'historique*

  **Calcul des Amendes**

  * *Tarif : 0.50€ par jour de retard*
  
  * *Calcul automatique : Dès le premier jour de retard*
  
  * *Affichage : Dans le dashboard et la fiche emprunt*

  # Diagramme de Classes
  ![Diagramme de Classes](https://github.com/user-attachments/assets/f5d8b7b1-1251-4008-ae08-ad5396d27502)

  # Diagramme de séquencee
  ![Diagramme de sequence](https://github.com/user-attachments/assets/7dd87a32-2ce5-4e0f-bc4f-0486fe0efb24)

  # Diagramme de cas d'utilisation: emprunt
  ![Use Case: emprunt](https://github.com/user-attachments/assets/7119405b-ca08-4db8-8b52-df410e57b3b8)

  ## Le projet est conçu pour être extensible. Les prochaines étapes de développement possibles sont :
* **Système d'authentification** : Ajout de rôles (Administrateur/Utilisateur) avec sécurité renforcée.
* **Génération de rapports** : Exportation des statistiques d'emprunt au format PDF.
* ...

  ## ⚖️ Licence
Ce projet est sous licence **MIT**. Tout le monde peut librement utiliser, modifier et distribuer ce code, à condition de citer l'auteur original et de conserver la mention de la licence.
---
*Ce projet est réalisé dans un cadre éducatif et est ouvert à toute suggestion ou contribution via les "Issues" ou "Pull Requests".*
