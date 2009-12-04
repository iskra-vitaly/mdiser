## Diffuse light calc

function [II] = diffuseLight(NN, L)
  wh = size(NN)(2:3);
  II = zeros(wh);
  N = NN(:,:);
  LIGHTSUM = sum(max(N'*normalizeVectors(L),0), 2);
  minmax = [min(LIGHTSUM) max(LIGHTSUM)];
  II(:)=(LIGHTSUM-minmax(1))/(minmax(2)-minmax(1));
end