select	documentoId, processoId
from	ProcessoDocumento pd
where	pd.cpf = ? or pd.cpf is null