## compute weighted combination of diffuse lights

function [II] = weightedDiffuse(NN, L, W)
  wh = size(NN)(2:3);
  II = zeros(wh);
  N = NN(:,:);
  weighted = ones(3,1)*W';
  L = normalizeVectors(L).*weighted;
  LIGHTSUM = sum(max(N'*L, 0), 2);
  minmax = [min(LIGHTSUM) max(LIGHTSUM)];
  II(:)=(LIGHTSUM-minmax(1))/(minmax(2)-minmax(1));
end