INSERT INTO usuario (nome, email, senha) VALUES ('Mario Geraldo', 'mario@hortech.com', '123456');

INSERT INTO horta (nome_horta, umidade_limite, temperatura_limite, luminosidade_limite, modo_automacao, tempo_irrigacao_padrao) 
VALUES ('Horta Principal - Hortech', 45, 30, 600, 'AUTOMATICO', 300);

INSERT INTO relacionamento_usuario_horta (id_usuario, id_horta, papel) VALUES (1, 1, 'PROPRIETARIO');


INSERT INTO notificacao (id_horta, data_hora, assunto_alerta) 
VALUES (1, CURRENT_TIMESTAMP, 'Sistema inicializado com sucesso');
