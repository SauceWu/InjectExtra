package me.sauce;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class BindExtraField {

    private VariableElement mFieldElement;
    private String mKey;

    BindExtraField(Element element) throws IllegalArgumentException {
        if (element.getKind() != ElementKind.FIELD) {
            throw new IllegalArgumentException(
                    String.format("Only fields can be annotated with @%s", InjectExtra.class.getSimpleName()));
        }

        mFieldElement = (VariableElement) element;
        InjectExtra bindView = mFieldElement.getAnnotation(InjectExtra.class);
        mKey = bindView.key();
    }

    String getKey() {
        return mKey;
    }

    TypeMirror getFieldType() {
        return mFieldElement.asType();
    }

    Name getFieldName() {
        return mFieldElement.getSimpleName();
    }
}
