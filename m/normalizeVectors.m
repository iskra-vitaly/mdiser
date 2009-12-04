## for each column v of V divide it by sqrt(v^2)

function [normalized] = normalizeVectors(V)
  lengths = sqrt(dot(V, V));
  normalized=V./(ones(size(V,1), 1)*lengths);
end