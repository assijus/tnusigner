select	pd.documentoId, pd.processoId,
SUBSTR(pro.numeroProcesso, 1, 7) || '-' || SUBSTR(pro.numeroProcesso, 8, 2) || '.' || SUBSTR(pro.numeroProcesso, 10, 4) || '.' || SUBSTR(pro.numeroProcesso, 14, 1) || '.' || SUBSTR(pro.numeroProcesso, 15, 2) || '.' || SUBSTR(pro.numeroProcesso, 17, 4) as numeroProcesso,
pd.descricao, pd.tipoDocumentoDescricao
from ProcessoDocumento pd left outer join Processo pro on pd.processoid = pro.id
where pd.cpf = ? and pd.attached is null and pro.situacao = 2
