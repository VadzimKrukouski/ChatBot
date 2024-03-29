package org.example.chatbot.telegram.nonCommand;

import org.example.chatbot.exceptions.IllegalSettingsException;

public class SettingsAssistant {
    /**
     * Расчёт минимального значения из двух чисел, заданных пользователем
     */
    static int calculateMin(int min, int max) {
        if (min == max) {
            throw new IllegalSettingsException("\uD83D\uDCA9 Совпадение минимального и максимального значений " +
                    "недопустимо");
        }
        return Math.min(min, max);
    }

    /**
     * Расчёт максимального значения из двух чисел, заданных пользователем. Если пользователь перепутал максимальное и
     * минимальное число, они меняются местами
     */
    static int calculateMax(int min, int max) {
        return Math.max(min, max);
    }

    /**
     * Расчёт значения числу страниц выгружаемого файла. Если пользователь установил значение больше 10,
     * присваивается значение 10, чтобы не перегружать сервис и не затягивать с ответом
     */
    static int calculateListCount(int listCount) {
        return Math.min(listCount, 10);
    }

    /**
     * Расчёт числа уникальных задач на сложение/вычитание, которые можно сформировать с использованием интервала
     * чисел от min до max
     */
    static int calculatePlusMinusUniqueTaskCount(int min, int max) {
        if (max - 2 * min + 1 >= 0) {
            return ((max - 2 * min + 2) * (max - 2 * min + 1)) / 2;
        }
        return 0;
    }

    /**
     * Расчёт числа уникальных задач на умножение, которыне можно сформировать с использованием интервала
     * чисел от min до max
     * Деление итогового результата на 2 введено специально для увеличения быстродействия
     */
    static int calculateMultiplicationUniqueTaskCount(int min, int max) {
        if (max < 10) {
            return (((max - min + 1) * 10) * 2 - (max - min + 1)) / 2;
        } else {
            return (((max - min + 1) * max) * 2 - (max - min + 1)) / 2;
        }
    }

    /**
     * Расчёт числа уникальных задач на деление, которыне можно сформировать с использованием интервала
     * чисел от min до max
     * Деление итогового результата на 2 введено специально для увеличения быстродействия
     */
    static int calculateDivisionUniqueTaskCount(int min, int max) {
        if (max < 10) {
            return (((max - min + 1) * 10) - (max - min + 1)) / 2;
        } else {
            return (((max - min + 1) * max) - (max - min + 1)) / 2;
        }
    }
}
