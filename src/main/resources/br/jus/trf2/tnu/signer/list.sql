select	pd.documentoId, pd.processoId,
concat(SUBSTRING(pro.numeroProcesso, 1, 7), '-', SUBSTRING(pro.numeroProcesso, 8, 2), '.',SUBSTRING(pro.numeroProcesso, 10, 4),'.',SUBSTRING(pro.numeroProcesso, 14, 1), '.',SUBSTRING(pro.numeroProcesso, 15, 2), '.',SUBSTRING(pro.numeroProcesso, 17, 4)) as numeroProcesso, 
pd.descricao, pd.tipoDocumentoDescricao
from ProcessoDocumento pd left outer join Processo pro on pd.processoid = pro.id
where	pd.cpf = ? or pd.cpf is null
