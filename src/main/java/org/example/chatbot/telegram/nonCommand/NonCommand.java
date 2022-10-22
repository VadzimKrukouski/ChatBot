package org.example.chatbot.telegram.nonCommand;

import org.example.chatbot.exceptions.IllegalSettingsException;
import org.example.chatbot.telegram.Bot;

public class NonCommand {

    public String nonCommandExecute(Long chatId, String userName, String text) {
        Settings settings;
        String answer;
        try {
            //создаём настройки из сообщения пользователя
            settings = createSettings(text);
            //добавляем настройки в мапу, чтобы потом их использовать для этого пользователя при генерации файла
            saveUserSettings(chatId, settings);
            answer = "Настройки обновлены. Вы всегда можете их посмотреть с помощью /settings";
            //логируем событие, используя userName
        } catch (IllegalSettingsException e) {
            answer = e.getMessage() +
                    "\n\n Настройки не были изменены. Вы всегда можете их посмотреть с помощью /settings";
            //логируем событие, используя userName
        } catch (Exception e) {
            answer = "Простите, я не понимаю Вас. Возможно, Вам поможет /help";
            //логируем событие, используя userName
        }
        return answer;
    }

    /**
     * Создание настроек из полученного пользователем сообщения
     * @param text текст сообщения
     * @throws IllegalArgumentException пробрасывается, если сообщение пользователя не соответствует формату
     */
    private Settings createSettings(String text) throws IllegalArgumentException {
        //отсекаем файлы, стикеры, гифки и прочий мусор
        if (text == null) {
            throw new IllegalArgumentException("Сообщение не является текстом");
        }
        //создаём из сообщения пользователя 3 числа-настройки (min, max, listCount) либо пробрасываем исключение о несоответствии сообщения требуемому формату
        text = text.replaceAll("-", "")//избавляемся от отрицательных чисел (умники найдутся)
                .replaceAll(", ", ",")//меняем ошибочный разделитель "запятая+пробел" на запятую
                .replaceAll(" ", ",");//меняем разделитель-пробел на запятую
        String[] parameters = text.split(",");
        if (parameters.length != 3) {
            throw new IllegalArgumentException(String.format("Не удалось разбить сообщение \"%s\" на 3 составляющих",
                    text));
        }
        int min = Integer.parseInt(parameters[0]);
        int max = Integer.parseInt(parameters[1]);
        int listCount = Integer.parseInt(parameters[2]);

        validateSettings(min, max, listCount);
        return new Settings(min, max, listCount);
    }

    /**
     * Валидация настроек
     */
    private void validateSettings(int min, int max, int listCount) {
        if (min == 0 || max == 0 || listCount == 0) {
            throw new IllegalSettingsException("\uD83D\uDCA9 Ни один из параметров не может равняться 0. Поражён " +
                    "Вашей неудачей");
        }
        if (min > 10000 || max > 10000) {
            throw new IllegalSettingsException("\uD83D\uDCA9 Заданные значения слишком велики. Не забывайте, мы учим " +
                    "детей устному счёту, а не рассчитываем траекторию полёта к Юпитеру");
        }
    }

    /**
     * Добавление настроек пользователя в мапу, чтобы потом их использовать для этого пользователя при генерации файла
     * Если настройки совпадают с дефолтными, они не сохраняются, чтобы впустую не раздувать мапу
     * @param chatId id чата
     * @param settings настройки
     */
    private void saveUserSettings(Long chatId, Settings settings) {
        if (!settings.equals(Bot.getDefaultSettings())) {
            Bot.getUserSettings().put(chatId, settings);
        } else {
            Bot.getUserSettings().remove(chatId);
        }
    }
}
