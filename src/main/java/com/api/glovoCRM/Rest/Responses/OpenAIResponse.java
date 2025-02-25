package com.api.glovoCRM.Rest.Responses;


import com.api.glovoCRM.Models.TelegramBotModels.Choice;
import com.api.glovoCRM.Models.TelegramBotModels.ErrorDetail;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Getter
@Setter
public class OpenAIResponse {

    private List<Choice> choices;

    private ErrorDetail error;
}



