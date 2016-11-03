select	conteudo, sh256 as secret
from	ProcessoDocumento pd
where	processoId = ? and documentoId = ?