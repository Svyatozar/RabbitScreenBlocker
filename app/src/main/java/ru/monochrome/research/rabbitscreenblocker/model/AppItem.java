package ru.monochrome.research.rabbitscreenblocker.model;

/**
 * Класс описывающий приложение в системе
 * @author konservator_007
 *
 */
public class AppItem
{
    public boolean is_checked;
    /**
     * Имя приложения
     */
    public String app_name;
    /**
     * Пакет приложения
     */
    public String app_package;

    public AppItem(boolean is_checked, String app_name, String app_package)
    {
        this.is_checked = is_checked;
        this.app_name = app_name;
        this.app_package = app_package;
    }

    public void setChecked(boolean is_checked)
    {
        this.is_checked = is_checked;
    }
}
