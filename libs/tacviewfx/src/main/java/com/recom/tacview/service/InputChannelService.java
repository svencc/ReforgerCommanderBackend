package com.recom.tacview.service;

import com.recom.observer.HasSubject;
import com.recom.observer.Subjective;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public class InputChannelService implements HasSubject {

    @Getter
    @NonNull
    private Subjective subject;

}
