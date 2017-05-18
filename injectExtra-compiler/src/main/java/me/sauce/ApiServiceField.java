package me.sauce;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;

/**
 * Created by sauce on 2017/5/15.
 * Version 1.0.0
 */
public class ApiServiceField {
    private Element mFieldElement;
    private String mKey;

    ApiServiceField(Element element) throws IllegalArgumentException {
        if (element.getKind() != ElementKind.FIELD) {
            throw new IllegalArgumentException(
                    String.format("Only fields can be annotated with @%s", InjectExtra.class.getSimpleName()));
        }

        mFieldElement = element;
        InjectExtra bindView = mFieldElement.getAnnotation(InjectExtra.class);
        mKey = bindView.value();
    }

    String getKey() {
        return mKey;
    }

    TypeMirror getFieldType() {
        return mFieldElement.asType();
    }

    List<? extends Element> getElements() {
        return mFieldElement.getEnclosedElements();
    }


    Name getFieldName() {
        return mFieldElement.getSimpleName();
    }
}
