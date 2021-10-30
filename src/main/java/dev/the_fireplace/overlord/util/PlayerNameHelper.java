package dev.the_fireplace.overlord.util;

import java.util.regex.Pattern;

public class PlayerNameHelper
{
    public static final Pattern VALID_NAME_REGEX = Pattern.compile("^\\w{3,16}$");
}
