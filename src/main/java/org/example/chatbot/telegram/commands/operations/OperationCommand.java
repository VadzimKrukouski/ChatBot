package org.example.chatbot.telegram.commands.operations;

import org.example.chatbot.calculation.ArithmeticService;
import org.example.chatbot.calculation.Calculator;
import org.example.chatbot.enums.OperationEnum;
import org.example.chatbot.fileProcessor.WordFileProcessorImpl;
import org.example.chatbot.telegram.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

abstract class OperationCommand extends BotCommand {
    private final Logger logger = LoggerFactory.getLogger(OperationCommand.class);
    private final ArithmeticService service;

    public OperationCommand(String identifier, String description) {
        super(identifier, description);
        this.service = new ArithmeticService(new WordFileProcessorImpl(), new Calculator());
    }

    /**
     * Отправка ответа пользователю
     */
    void sendAnswer(AbsSender absSender, Long chatId, List<OperationEnum> operations, String description,
                    String commandName, String userName) {
        try {
            absSender.execute(createDocument(chatId, operations, description));
        } catch (IOException | RuntimeException e) {
            logger.error(String.format("Ошибка %s. Команда %s. Пользователь: %s", e.getMessage(), commandName, userName));
            sendError(absSender, chatId, commandName, userName);
            e.printStackTrace();
        } catch (TelegramApiException e) {
            logger.error(String.format("Ошибка %s. Команда %s. Пользователь: %s", e.getMessage(), commandName, userName));
            e.printStackTrace();
        }
    }

    /**
     * Создание документа для отправки пользователю
     * @param chatId id чата
     * @param operations список типов операций (сложение и/или вычитание)
     * @param fileName имя, которое нужно присвоить файлу
     */
    private SendDocument createDocument(Long chatId, List<OperationEnum> operations, String fileName) throws IOException {
        FileInputStream stream = service.getFile(operations, Bot.getUserSettings(chatId));
        SendDocument document = new SendDocument();
        document.setChatId(chatId.toString());
        document.setDocument(new InputFile(stream, String.format("%s.docx", fileName)));
        return document;
    }

    /**
     * Отправка пользователю сообщения об ошибке
     */
    private void sendError(AbsSender absSender, Long chatId, String commandName, String userName) {
        try {
            absSender.execute(new SendMessage(chatId.toString(), "Похоже, я сломался. Попробуйте позже"));
        } catch (TelegramApiException e) {
            logger.error(String.format("Ошибка %s. Команда %s. Пользователь: %s", e.getMessage(), commandName, userName));
            e.printStackTrace();
        }
    }
}
