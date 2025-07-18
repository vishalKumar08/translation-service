-- Insert sample tags
INSERT INTO tags (name, description) VALUES
('mobile', 'Mobile application translations'),
('desktop', 'Desktop application translations'),
('web', 'Web application translations'),
('admin', 'Admin panel translations'),
('user', 'User interface translations'),
('error', 'Error message translations'),
('validation', 'Form validation translations'),
('navigation', 'Navigation menu translations'),
('button', 'Button text translations'),
('label', 'Form label translations');

-- Insert sample translations for English
INSERT INTO translations (translation_key, locale, content) VALUES
-- App general
('app.title', 'en', 'Translation Management System'),
('app.welcome', 'en', 'Welcome to our application'),
('app.loading', 'en', 'Loading...'),
('app.save', 'en', 'Save'),
('app.cancel', 'en', 'Cancel'),
('app.delete', 'en', 'Delete'),
('app.edit', 'en', 'Edit'),
('app.create', 'en', 'Create'),
('app.search', 'en', 'Search'),
('app.filter', 'en', 'Filter'),

-- Authentication
('auth.login', 'en', 'Login'),
('auth.logout', 'en', 'Logout'),
('auth.username', 'en', 'Username'),
('auth.password', 'en', 'Password'),
('auth.forgot_password', 'en', 'Forgot Password?'),
('auth.remember_me', 'en', 'Remember Me'),

-- Navigation
('nav.home', 'en', 'Home'),
('nav.dashboard', 'en', 'Dashboard'),
('nav.translations', 'en', 'Translations'),
('nav.tags', 'en', 'Tags'),
('nav.settings', 'en', 'Settings'),
('nav.profile', 'en', 'Profile'),

-- Errors
('error.not_found', 'en', 'Resource not found'),
('error.unauthorized', 'en', 'Unauthorized access'),
('error.server_error', 'en', 'Internal server error'),
('error.validation_failed', 'en', 'Validation failed'),

-- Validation
('validation.required', 'en', 'This field is required'),
('validation.email', 'en', 'Please enter a valid email address'),
('validation.min_length', 'en', 'Minimum length is {0} characters'),
('validation.max_length', 'en', 'Maximum length is {0} characters');

-- Insert sample translations for French
INSERT INTO translations (translation_key, locale, content) VALUES
-- App general
('app.title', 'fr', 'Système de Gestion des Traductions'),
('app.welcome', 'fr', 'Bienvenue dans notre application'),
('app.loading', 'fr', 'Chargement...'),
('app.save', 'fr', 'Enregistrer'),
('app.cancel', 'fr', 'Annuler'),
('app.delete', 'fr', 'Supprimer'),
('app.edit', 'fr', 'Modifier'),
('app.create', 'fr', 'Créer'),
('app.search', 'fr', 'Rechercher'),
('app.filter', 'fr', 'Filtrer'),

-- Authentication
('auth.login', 'fr', 'Connexion'),
('auth.logout', 'fr', 'Déconnexion'),
('auth.username', 'fr', 'Nom d''utilisateur'),
('auth.password', 'fr', 'Mot de passe'),
('auth.forgot_password', 'fr', 'Mot de passe oublié?'),
('auth.remember_me', 'fr', 'Se souvenir de moi'),

-- Navigation
('nav.home', 'fr', 'Accueil'),
('nav.dashboard', 'fr', 'Tableau de bord'),
('nav.translations', 'fr', 'Traductions'),
('nav.tags', 'fr', 'Étiquettes'),
('nav.settings', 'fr', 'Paramètres'),
('nav.profile', 'fr', 'Profil'),

-- Errors
('error.not_found', 'fr', 'Ressource non trouvée'),
('error.unauthorized', 'fr', 'Accès non autorisé'),
('error.server_error', 'fr', 'Erreur interne du serveur'),
('error.validation_failed', 'fr', 'Échec de la validation'),

-- Validation
('validation.required', 'fr', 'Ce champ est obligatoire'),
('validation.email', 'fr', 'Veuillez saisir une adresse e-mail valide'),
('validation.min_length', 'fr', 'La longueur minimale est de {0} caractères'),
('validation.max_length', 'fr', 'La longueur maximale est de {0} caractères');

-- Insert sample translations for Spanish
INSERT INTO translations (translation_key, locale, content) VALUES
-- App general
('app.title', 'es', 'Sistema de Gestión de Traducciones'),
('app.welcome', 'es', 'Bienvenido a nuestra aplicación'),
('app.loading', 'es', 'Cargando...'),
('app.save', 'es', 'Guardar'),
('app.cancel', 'es', 'Cancelar'),
('app.delete', 'es', 'Eliminar'),
('app.edit', 'es', 'Editar'),
('app.create', 'es', 'Crear'),
('app.search', 'es', 'Buscar'),
('app.filter', 'es', 'Filtrar'),

-- Authentication
('auth.login', 'es', 'Iniciar sesión'),
('auth.logout', 'es', 'Cerrar sesión'),
('auth.username', 'es', 'Nombre de usuario'),
('auth.password', 'es', 'Contraseña'),
('auth.forgot_password', 'es', '¿Olvidaste tu contraseña?'),
('auth.remember_me', 'es', 'Recordarme'),

-- Navigation
('nav.home', 'es', 'Inicio'),
('nav.dashboard', 'es', 'Panel de control'),
('nav.translations', 'es', 'Traducciones'),
('nav.tags', 'es', 'Etiquetas'),
('nav.settings', 'es', 'Configuración'),
('nav.profile', 'es', 'Perfil'),

-- Errors
('error.not_found', 'es', 'Recurso no encontrado'),
('error.unauthorized', 'es', 'Acceso no autorizado'),
('error.server_error', 'es', 'Error interno del servidor'),
('error.validation_failed', 'es', 'Falló la validación'),

-- Validation
('validation.required', 'es', 'Este campo es obligatorio'),
('validation.email', 'es', 'Por favor ingrese una dirección de correo válida'),
('validation.min_length', 'es', 'La longitud mínima es de {0} caracteres'),
('validation.max_length', 'es', 'La longitud máxima es de {0} caracteres');

-- Associate translations with tags
INSERT INTO translation_tags (translation_id, tag_id)
SELECT t.id, tag.id
FROM translations t
CROSS JOIN tags tag
WHERE 
    (t.translation_key LIKE 'app.%' AND tag.name IN ('web', 'mobile', 'desktop')) OR
    (t.translation_key LIKE 'auth.%' AND tag.name IN ('user', 'web')) OR
    (t.translation_key LIKE 'nav.%' AND tag.name IN ('navigation', 'web')) OR
    (t.translation_key LIKE 'error.%' AND tag.name IN ('error', 'web')) OR
    (t.translation_key LIKE 'validation.%' AND tag.name IN ('validation', 'web')) OR
    (t.translation_key IN ('app.save', 'app.cancel', 'app.delete', 'app.edit', 'app.create') AND tag.name = 'button') OR
    (t.translation_key LIKE 'auth.username' OR t.translation_key LIKE 'auth.password' AND tag.name = 'label');
