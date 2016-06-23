select	pd.documentoId, pd.processoId,
pro.numerocommascara as numeroProcesso,
pd.descricao, tp.descricao as tipoDocumentoDescricao
from ProcessoDocumento pd left outer join Processo pro on pd.processoid = pro.id 
left outer join tiposdocumentoprocessual tp on pd.tipodocumentoid = tp.codigo
left outer join usuario usu on usu.idorgao = pro.idorgao
where usu.cpf = ? and usu.assinadoc = 'S' and pd.attached is null and pro.situacao = 2