package com.resale.homeflyuser.logging;

import com.resale.homeflyuser.model.ActionType;
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogActivity {
    ActionType value();
}


