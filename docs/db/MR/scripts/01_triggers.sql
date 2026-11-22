USE gerenciamento_rpg;

DROP TRIGGER IF EXISTS ANOTACAO_BEFORE_INSERT;
DROP TRIGGER IF EXISTS SESSAO_BEFORE_INSERT;
DROP TRIGGER IF EXISTS FICHAS_TEMPLATE_BEFORE_INSERT;

DELIMITER $$

CREATE TRIGGER ANOTACAO_BEFORE_INSERT
BEFORE INSERT ON ANOTACAO
FOR EACH ROW
BEGIN
    IF NEW.numero_anotacao = 0 THEN
        SET NEW.numero_anotacao = (
            SELECT COALESCE(MAX(numero_anotacao), 0) + 1
            FROM ANOTACAO
            WHERE id_usuario = NEW.id_usuario
        );
    END IF;
END$$

CREATE TRIGGER SESSAO_BEFORE_INSERT
BEFORE INSERT ON SESSAO
FOR EACH ROW
BEGIN
    IF NEW.numero_sessao = 0 THEN
        SET NEW.numero_sessao = (
            SELECT COALESCE(MAX(numero_sessao), 0) + 1
            FROM SESSAO
            WHERE id_campanha = NEW.id_campanha
        );
    END IF;
END$$

CREATE TRIGGER FICHAS_TEMPLATE_BEFORE_INSERT
BEFORE INSERT ON FICHAS_TEMPLATE
FOR EACH ROW
BEGIN
    IF NEW.numero_ficha = 0 THEN
        SET NEW.numero_ficha = (
            SELECT COALESCE(MAX(numero_ficha), 0) + 1
            FROM FICHAS_TEMPLATE
            WHERE id_sistema = NEW.id_sistema
        );
    END IF;
END$$

DELIMITER ;
