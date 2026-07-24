ALTER TABLE categoria
ADD COLUMN setor_id BIGINT;

ALTER TABLE categoria
ADD CONSTRAINT fk_categoria_setor
FOREIGN KEY (setor_id)
REFERENCES setor(id);