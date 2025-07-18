package com.digitaltolk.translationservice.command;

import com.digitaltolk.translationservice.domain.entity.Tag;
import com.digitaltolk.translationservice.domain.entity.Translation;
import com.digitaltolk.translationservice.domain.repository.TagRepository;
import com.digitaltolk.translationservice.domain.repository.TranslationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "app.data-seeder.enabled", havingValue = "true")
public class DataSeederCommand implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeederCommand.class);

    private final TranslationRepository translationRepository;
    private final TagRepository tagRepository;

    private static final String[] LOCALES = {"en", "fr", "es", "de", "it", "pt", "ru", "zh", "ja", "ko"};
    private static final String[] CONTEXTS = {"app", "auth", "nav", "error", "validation", "form", "button", "label",
            "message", "notification", "dialog", "menu", "table", "chart", "report", "dashboard", "profile",
            "settings", "admin", "user", "product", "order", "payment", "shipping", "invoice", "customer",
            "support", "help", "faq"};
    private static final String[] ACTIONS = {"create", "read", "update", "delete", "save", "cancel", "submit", "reset",
            "search", "filter", "sort", "export", "import", "download", "upload", "edit", "view", "list",
            "detail", "summary", "total", "count", "status"};
    private static final String[] COMPONENTS = {"title", "subtitle", "header", "footer", "sidebar", "content", "body",
            "text", "description", "placeholder", "tooltip", "hint", "warning", "success", "info", "loading",
            "empty", "nodata", "required", "optional"};

    public DataSeederCommand(TranslationRepository translationRepository, TagRepository tagRepository) {
        this.translationRepository = translationRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("Starting data seeding process...");

        long startTime = System.currentTimeMillis();

        List<Tag> existingTags = tagRepository.findAll();
        logger.info("Found {} existing tags", existingTags.size());

        int batchSize = 1000;
        int totalRecords = 100000;
        int batches = totalRecords / batchSize;

        logger.info("Generating {} translations in {} batches of {}", totalRecords, batches, batchSize);

        for (int batch = 0; batch < batches; batch++) {
            List<Translation> translations = generateTranslationBatch(batchSize, existingTags);
            translationRepository.saveAll(translations);

            if ((batch + 1) % 10 == 0) {
                logger.info("Completed batch {} of {} ({} translations)", batch + 1, batches, (batch + 1) * batchSize);
            }
        }

        long endTime = System.currentTimeMillis();
        logger.info("Data seeding completed in {} ms", (endTime - startTime));
        logStatistics();
    }

    private List<Translation> generateTranslationBatch(int batchSize, List<Tag> availableTags) {
        List<Translation> translations = new ArrayList<>();

        for (int i = 0; i < batchSize; i++) {
            String key = generateUniqueTranslationKey();
            String locale = LOCALES[ThreadLocalRandom.current().nextInt(LOCALES.length)];
            String content = generateTranslationContent(key, locale);

            Translation translation = new Translation(key, locale, content);

            if (!availableTags.isEmpty()) {
                int tagCount = ThreadLocalRandom.current().nextInt(4);
                Set<Tag> selectedTags = new HashSet<>();
                for (int j = 0; j < tagCount; j++) {
                    Tag randomTag = availableTags.get(ThreadLocalRandom.current().nextInt(availableTags.size()));
                    selectedTags.add(randomTag);
                }
                translation.setTags(selectedTags);
            }

            translations.add(translation);
        }

        return translations;
    }

    private String generateUniqueTranslationKey() {
        String context = CONTEXTS[ThreadLocalRandom.current().nextInt(CONTEXTS.length)];
        String action = ACTIONS[ThreadLocalRandom.current().nextInt(ACTIONS.length)];
        String component = COMPONENTS[ThreadLocalRandom.current().nextInt(COMPONENTS.length)];
        String baseKey = String.join(".", context, action, component);
        return baseKey + "." + UUID.randomUUID();
    }

    private String generateTranslationContent(String key, String locale) {
        String[] parts = key.split("\\.");
        String component = parts[parts.length - 2];

        return switch (locale) {
            case "en" -> generateEnglishContent(component);
            case "fr" -> generateFrenchContent(component);
            case "es" -> generateSpanishContent(component);
            case "de" -> generateGermanContent(component);
            case "it" -> generateItalianContent(component);
            case "pt" -> generatePortugueseContent(component);
            case "ru" -> generateRussianContent(component);
            case "zh" -> generateChineseContent(component);
            case "ja" -> generateJapaneseContent(component);
            case "ko" -> generateKoreanContent(component);
            default -> "Translation for " + key + " in " + locale;
        };
    }

    // Language-specific content generation
    private String generateEnglishContent(String component) {
        return switch (component) {
            case "title" -> "Title " + ThreadLocalRandom.current().nextInt(1000);
            case "button" -> "Click Here";
            case "label" -> "Field Label";
            case "placeholder" -> "Enter value here...";
            case "error" -> "An error occurred";
            case "success" -> "Operation completed successfully";
            case "loading" -> "Loading...";
            case "save" -> "Save";
            case "cancel" -> "Cancel";
            case "delete" -> "Delete";
            case "create" -> "Create New";
            case "edit" -> "Edit";
            case "view" -> "View Details";
            default -> "Content for " + component;
        };
    }

    private String generateFrenchContent(String component) {
        return switch (component) {
            case "title" -> "Titre " + ThreadLocalRandom.current().nextInt(1000);
            case "button" -> "Cliquez ici";
            case "label" -> "Étiquette de champ";
            case "placeholder" -> "Entrez la valeur ici...";
            case "error" -> "Une erreur s'est produite";
            case "success" -> "Opération terminée avec succès";
            case "loading" -> "Chargement...";
            case "save" -> "Enregistrer";
            case "cancel" -> "Annuler";
            case "delete" -> "Supprimer";
            case "create" -> "Créer nouveau";
            case "edit" -> "Modifier";
            case "view" -> "Voir les détails";
            default -> "Contenu pour " + component;
        };
    }

    private String generateSpanishContent(String component) {
        return switch (component) {
            case "title" -> "Título " + ThreadLocalRandom.current().nextInt(1000);
            case "button" -> "Haz clic aquí";
            case "label" -> "Etiqueta de campo";
            case "placeholder" -> "Ingrese valor aquí...";
            case "error" -> "Ocurrió un error";
            case "success" -> "Operación completada exitosamente";
            case "loading" -> "Cargando...";
            case "save" -> "Guardar";
            case "cancel" -> "Cancelar";
            case "delete" -> "Eliminar";
            case "create" -> "Crear nuevo";
            case "edit" -> "Editar";
            case "view" -> "Ver detalles";
            default -> "Contenido para " + component;
        };
    }

    private String generateGermanContent(String component) {
        return switch (component) {
            case "title" -> "Titel " + ThreadLocalRandom.current().nextInt(1000);
            case "button" -> "Hier klicken";
            case "label" -> "Feldbezeichnung";
            case "placeholder" -> "Wert hier eingeben...";
            case "error" -> "Ein Fehler ist aufgetreten";
            case "success" -> "Vorgang erfolgreich abgeschlossen";
            case "loading" -> "Wird geladen...";
            case "save" -> "Speichern";
            case "cancel" -> "Abbrechen";
            case "delete" -> "Löschen";
            case "create" -> "Neu erstellen";
            case "edit" -> "Bearbeiten";
            case "view" -> "Details anzeigen";
            default -> "Inhalt für " + component;
        };
    }

    private String generateItalianContent(String component) {
        return switch (component) {
            case "title" -> "Titolo " + ThreadLocalRandom.current().nextInt(1000);
            case "button" -> "Clicca qui";
            case "label" -> "Etichetta campo";
            case "placeholder" -> "Inserisci valore qui...";
            case "error" -> "Si è verificato un errore";
            case "success" -> "Operazione completata con successo";
            case "loading" -> "Caricamento...";
            case "save" -> "Salva";
            case "cancel" -> "Annulla";
            case "delete" -> "Elimina";
            case "create" -> "Crea nuovo";
            case "edit" -> "Modifica";
            case "view" -> "Visualizza dettagli";
            default -> "Contenuto per " + component;
        };
    }

    private String generatePortugueseContent(String component) {
        return switch (component) {
            case "title" -> "Título " + ThreadLocalRandom.current().nextInt(1000);
            case "button" -> "Clique aqui";
            case "label" -> "Rótulo do campo";
            case "placeholder" -> "Digite o valor aqui...";
            case "error" -> "Ocorreu um erro";
            case "success" -> "Operação concluída com sucesso";
            case "loading" -> "Carregando...";
            case "save" -> "Salvar";
            case "cancel" -> "Cancelar";
            case "delete" -> "Excluir";
            case "create" -> "Criar novo";
            case "edit" -> "Editar";
            case "view" -> "Ver detalhes";
            default -> "Conteúdo para " + component;
        };
    }

    private String generateRussianContent(String component) {
        return switch (component) {
            case "title" -> "Заголовок " + ThreadLocalRandom.current().nextInt(1000);
            case "button" -> "Нажмите здесь";
            case "label" -> "Метка поля";
            case "placeholder" -> "Введите значение здесь...";
            case "error" -> "Произошла ошибка";
            case "success" -> "Операция успешно завершена";
            case "loading" -> "Загрузка...";
            case "save" -> "Сохранить";
            case "cancel" -> "Отмена";
            case "delete" -> "Удалить";
            case "create" -> "Создать новый";
            case "edit" -> "Редактировать";
            case "view" -> "Просмотр деталей";
            default -> "Содержимое для " + component;
        };
    }

    private String generateChineseContent(String component) {
        return switch (component) {
            case "title" -> "标题 " + ThreadLocalRandom.current().nextInt(1000);
            case "button" -> "点击这里";
            case "label" -> "字段标签";
            case "placeholder" -> "在此输入值...";
            case "error" -> "发生错误";
            case "success" -> "操作成功完成";
            case "loading" -> "加载中...";
            case "save" -> "保存";
            case "cancel" -> "取消";
            case "delete" -> "删除";
            case "create" -> "创建新的";
            case "edit" -> "编辑";
            case "view" -> "查看详情";
            default -> component + "的内容";
        };
    }

    private String generateJapaneseContent(String component) {
        return switch (component) {
            case "title" -> "タイトル " + ThreadLocalRandom.current().nextInt(1000);
            case "button" -> "ここをクリック";
            case "label" -> "フィールドラベル";
            case "placeholder" -> "ここに値を入力...";
            case "error" -> "エラーが発生しました";
            case "success" -> "操作が正常に完了しました";
            case "loading" -> "読み込み中...";
            case "save" -> "保存";
            case "cancel" -> "キャンセル";
            case "delete" -> "削除";
            case "create" -> "新規作成";
            case "edit" -> "編集";
            case "view" -> "詳細を表示";
            default -> component + "のコンテンツ";
        };
    }

    private String generateKoreanContent(String component) {
        return switch (component) {
            case "title" -> "제목 " + ThreadLocalRandom.current().nextInt(1000);
            case "button" -> "여기를 클릭";
            case "label" -> "필드 레이블";
            case "placeholder" -> "여기에 값을 입력하세요...";
            case "error" -> "오류가 발생했습니다";
            case "success" -> "작업이 성공적으로 완료되었습니다";
            case "loading" -> "로딩 중...";
            case "save" -> "저장";
            case "cancel" -> "취소";
            case "delete" -> "삭제";
            case "create" -> "새로 만들기";
            case "edit" -> "편집";
            case "view" -> "세부 정보 보기";
            default -> component + "에 대한 내용";
        };
    }

    private void logStatistics() {
        long totalTranslations = translationRepository.count();
        long totalTags = tagRepository.count();
        List<String> locales = translationRepository.findDistinctLocales();

        logger.info("=== Data Seeding Statistics ===");
        logger.info("Total translations: {}", totalTranslations);
        logger.info("Total tags: {}", totalTags);
        logger.info("Locales present: {}", locales);
        for (String locale : locales) {
            logger.info("  {}: {} entries", locale, translationRepository.countByLocale(locale));
        }
    }
}
