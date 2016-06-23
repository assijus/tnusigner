select	pd.documentoId, pd.processoId,
SUBSTR(pro.numeroProcesso, 1, 7) || '-' || SUBSTR(pro.numeroProcesso, 8, 2) || '.' || SUBSTR(pro.numeroProcesso, 10, 4) || '.' || SUBSTR(pro.numeroProcesso, 14, 1) || '.' || SUBSTR(pro.numeroProcesso, 15, 2) || '.' || SUBSTR(pro.numeroProcesso, 17, 4) as numeroProcesso,
pd.descricao, tp.descricao as tipoDocumentoDescricao
from ProcessoDocumento pd left outer join Processo pro on pd.processoid = pro.id 
left outer join tiposdocumentoprocessual tp on pd.tipodocumentoid = tp.codigo
left outer join usuario usu on usu.idorgao = pro.idorgao
where usu.cpf = ? and usu.assinadoc = 'S' and pd.attached is null and pro.situacao = 2